package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.PaymentRequest;
import com.ticket.concert.dto.PaymentResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.PaymentRepository;
import com.ticket.concert.repository.ReservationRepository;
import com.ticket.concert.repository.ReservationSeatRepository;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestClient restClient = RestClient.builder()
            .requestFactory(tossRequestFactory())
            .build();

    private static SimpleClientHttpRequestFactory tossRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        return factory;
    }

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;
    private final TransactionTemplate transactionTemplate;

    public void confirm(PaymentRequest request, Long userId) {
        List<Long> seatIds = transactionTemplate.execute(
                status -> validateForConfirm(request, userId));

        for (Long seatId : seatIds) {
            Long remain = redisTemplate.getExpire("seat:hold:" + seatId, TimeUnit.SECONDS);
            if (remain == null || remain < 10) {
                throw new CustomException(ErrorCode.SEAT_NOT_HOLDING);
            }
        }

        callTossConfirm(request);

        try {
            transactionTemplate.executeWithoutResult(status -> savePaid(request));
        } catch (CustomException e) {
            cancelQuietly(request.getPaymentKey(), "좌석 상태 변경으로 인한 자동 취소");
            throw e;
        } catch (Exception e) {
            log.error("결제 저장 실패 reservationId={}", request.getReservationId(), e);
            cancelQuietly(request.getPaymentKey(), "서버 저장 실패로 인한 자동 취소");
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        try {
            for (Long seatId : seatIds) {
                redisTemplate.delete("seat:hold:" + seatId);
            }
        } catch (Exception e) {
            log.warn("홀딩 키 삭제 실패, TTL로 자동 만료됨", e);
        }
    }

    private List<Long> validateForConfirm(PaymentRequest request, Long userId) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.RESERVATION_CANCELLED);
        }

        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        if (reservationSeats.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        if (request.getAmount() <= 0) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        long actualAmount = reservationSeats.stream()
                .mapToLong(rs -> rs.getScheduleSeat().getSeatGrade().getPrice()).sum();
        if (request.getAmount() != actualAmount) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        if (paymentRepository.existsByReservation(reservation)) {
            throw new CustomException(ErrorCode.ALREADY_PAID);
        }
        for (ReservationSeat rs : reservationSeats) {
            if (rs.getScheduleSeat().getStatus() != SeatStatus.HOLDING) {
                throw new CustomException(ErrorCode.SEAT_NOT_HOLDING);
            }
        }

        return reservationSeats.stream()
                .map(rs -> rs.getScheduleSeat().getId())
                .toList();
    }

    private void callTossConfirm(PaymentRequest request) {
        try {
            restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "paymentKey", request.getPaymentKey(),
                            "orderId", request.getOrderId(),
                            "amount", request.getAmount()
                    ))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientResponseException e) {
            log.warn("토스 승인 실패: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        } catch (RestClientException e) {
            log.error("토스 연결/타임아웃 reservationId={}", request.getReservationId(), e);
            cancelQuietly(request.getPaymentKey(), "승인 응답 시간 초과로 인한 자동 취소");
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }
    }

    private void savePaid(PaymentRequest request) {
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        for (ReservationSeat rs : reservationSeats) {
            if (rs.getScheduleSeat().getStatus() != SeatStatus.HOLDING) {
                throw new CustomException(ErrorCode.SEAT_NOT_HOLDING);
            }
        }

        Payment payment = new Payment(reservation, request.getAmount(), request.getPaymentKey());
        payment.complete();
        paymentRepository.save(payment);

        for (ReservationSeat rs : reservationSeats) {
            rs.getScheduleSeat().reserve();
        }
    }

    private void cancelQuietly(String paymentKey, String reason) {
        try {
            restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/{paymentKey}/cancel", paymentKey)
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cancelReason", reason))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("토스 자동 취소 실패 paymentKey={} 수동 확인 필요", paymentKey, e);
        }
    }

    private String encodedKey() {
        return Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> getMyPayments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return paymentRepository.findAllByReservation_User(user).stream()
                .map(PaymentResponse::from)
                .toList();
    }

    public void cancelByReservation(Reservation reservation, String reason) {
        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        cancel(payment, reason);
    }

    public void cancel(Payment payment, String reason) {
        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey())
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("cancelReason", reason))
                .retrieve()
                .toBodilessEntity();

        payment.refund();
    }

    public void refundAndSave(Long paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));

        try {
            restClient.post()
                    .uri("https://api.tosspayments.com/v1/payments/{paymentKey}/cancel", payment.getPaymentKey())
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("cancelReason", reason))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("환불 실패 paymentId={} 수동 확인 필요", paymentId, e);
            throw new CustomException(ErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        transactionTemplate.executeWithoutResult(status -> {
            Payment saved = paymentRepository.findById(paymentId)
                    .orElseThrow(() -> new CustomException(ErrorCode.PAYMENT_NOT_FOUND));
            saved.refund();
            reservationSeatRepository.findAllByReservation(saved.getReservation())
                    .forEach(rs -> rs.getScheduleSeat().release());
            saved.getReservation().cancel();
        });
    }
}