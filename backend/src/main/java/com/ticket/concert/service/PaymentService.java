package com.ticket.concert.service;

import com.ticket.concert.domain.Payment;
import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.ReservationSeat;
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
    public void confirm(PaymentRequest request) {
        log.info("PaymentService.confirm 진입");

        String encodedKey = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));


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
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        Payment payment = new Payment(reservation, request.getAmount());
        payment.complete();
        paymentRepository.save(payment);

        List<ReservationSeat> reservationSeats = reservationSeatRepository.findAllByReservation(reservation);

        for (ReservationSeat reservationSeat : reservationSeats) {
            reservationSeat.getScheduleSeat().reserve();
        }
    }
}
