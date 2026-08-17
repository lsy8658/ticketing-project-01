package com.ticket.concert.domain;

public enum SeatStatus {
    AVAILABLE,
    HOLDING,
    RESERVED
}

/*
    AVAILABLE : 예매 가능
    HOLDING : 결제 진행 중 임시 점유(5분)
    RESERVED : 결제 완료
*/