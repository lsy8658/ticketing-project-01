package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.PaymentRequest;
import com.ticket.concert.repository.PaymentRepository;
import com.ticket.concert.repository.ReservationRepository;
import com.ticket.concert.repository.ReservationSeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${toss.secret-key}")
    private String secretKey;


    private final RestClient restClient = RestClient.create();
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;


    @Transactional
    public void confirm(PaymentRequest request, Long userId) {
        log.info("PaymentService.confirm 진입");

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("취소된 예약은 결제할 수 없습니다.");
        }

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인의 예약만 결제할 수 있습니다.");
        }

        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));


        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        if (request.getAmount() <= 0) {
            throw new RuntimeException("결제 금액은 0원보다 커야 합니다.");
        }
        if (reservationSeats.isEmpty()) {
            throw new RuntimeException("예약된 좌석이 없습니다.");
        }
        long actualAmount = reservationSeats.stream()
                .mapToLong(reservationSeat -> reservationSeat.getScheduleSeat()
                        .getSeat().getSeatGrade().getPrice()).sum();

        if (request.getAmount() != actualAmount) {
            throw new RuntimeException("결제 금액이 올바르지 않습니다.");
        }

        if (paymentRepository.existsByReservation(reservation)) {
            throw new RuntimeException("이미 결제된 예약입니다.");
        }

        for (ReservationSeat reservationSeat: reservationSeats) {
            if (reservationSeat.getScheduleSeat().getStatus() != SeatStatus.HOLDING) {
                throw new RuntimeException("결제할 수 없는 좌석이 있습니다.");
            }
        }
        restClient.post()
                .uri("https://api.tosspayments.com/v1/payments/confirm")
                .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "paymentKey", request.getPaymentKey(),
                        "orderId", request.getOrderId(),
                        "amount", request.getAmount()
                ))
                .retrieve()
                .toBodilessEntity();


        Payment payment = new Payment(reservation, request.getAmount());
        payment.complete();
        paymentRepository.save(payment);


        for (ReservationSeat reservationSeat : reservationSeats) {
            reservationSeat.getScheduleSeat().reserve();
        }
    }
}
