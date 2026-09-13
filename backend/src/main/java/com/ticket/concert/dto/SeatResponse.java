package com.ticket.concert.dto;

import lombok.Getter;

@Getter
public class SeatResponse {
    private Long id;
    private String seatNumber;

    public SeatResponse(Long id, String seatNumber) {
        this.id = id;
        this.seatNumber = seatNumber;
    }
}