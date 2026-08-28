package com.ticket.concert.service;

import com.ticket.concert.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    @Value("${toss.secret-key}")
    private String secretKey;


    private final RestClient restClient = RestClient.create();


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
    }
}
