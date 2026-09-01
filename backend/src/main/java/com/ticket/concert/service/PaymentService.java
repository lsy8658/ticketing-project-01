package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.PaymentRequest;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.RESERVATION_CANCELLED);
        }

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.PAYMENT_FORBIDDEN);
        }

        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));


        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        if (request.getAmount() <= 0) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
        if (reservationSeats.isEmpty()) {
            throw new CustomException(ErrorCode.RESERVATION_NOT_FOUND);
        }
        long actualAmount = reservationSeats.stream()
                .mapToLong(reservationSeat -> reservationSeat.getScheduleSeat()
                        .getSeat().getSeatGrade().getPrice()).sum();

        if (request.getAmount() != actualAmount) {
            throw new CustomException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }

        if (paymentRepository.existsByReservation(reservation)) {
            throw new CustomException(ErrorCode.ALREADY_PAID);
        }

        for (ReservationSeat reservationSeat: reservationSeats) {
            if (reservationSeat.getScheduleSeat().getStatus() != SeatStatus.HOLDING) {
                throw new CustomException(ErrorCode.SEAT_NOT_HOLDING);
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
