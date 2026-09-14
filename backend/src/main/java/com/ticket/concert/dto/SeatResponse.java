package com.ticket.concert.dto;

import lombok.Getter;

@Getter
public class SeatResponse {
    private Long id;
    private String seatNumber;
    private int priority;
    private String rowName;

    public SeatResponse(Long id, String seatNumber, String rowName, int priority) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.rowName = rowName;
        this.priority = priority;
    }
}