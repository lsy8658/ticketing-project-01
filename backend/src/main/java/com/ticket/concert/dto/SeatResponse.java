package com.ticket.concert.dto;

public class SeatResponse {
    private Long id;
    private String seatNumber;
    private Long seatGradeId;

    public SeatResponse (Long id, String seatNumber, Long seatGradeId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatGradeId = seatGradeId;
    }
}
