package com.ticket.concert.dto;

import com.ticket.concert.domain.Payment;
import com.ticket.concert.domain.PaymentStatus;
import lombok.Getter;

@Getter
public class PaymentResponse {
    private Long id;
    private Long reservationId;
    private PaymentStatus status;
    private Long amount;

    public PaymentResponse(Long id, Long reservationId, PaymentStatus status, Long amount) {
        this.id = id;
        this.reservationId = reservationId;
        this.status = status;
        this.amount = amount;
    }

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getReservation().getId(),
                payment.getStatus(),
                payment.getAmount()
        );
    }
}