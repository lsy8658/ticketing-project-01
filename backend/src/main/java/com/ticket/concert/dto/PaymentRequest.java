package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PaymentRequest {

    @NotBlank(message = "결제 키는 필수입니다.")
    private String paymentKey;

    @NotBlank(message = "주문 ID는 필수입니다.")
    private String orderId;

    @NotNull(message = "결제 금액은 필수입니다.")
    private Long amount;

    @NotNull(message = "예약 ID는 필수입니다.")
    private Long reservationId;
}