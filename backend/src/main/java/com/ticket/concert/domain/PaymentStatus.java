package com.ticket.concert.domain;

public enum PaymentStatus {
    PENDING,
    PAID,
    FAILED,
    REFUNDED
}

/*
   PENDING : 결제 요청됨
   PAID : 결제 완료
   FAILED : 결제 실패
   REFUNDED : 환불 상태
*/
