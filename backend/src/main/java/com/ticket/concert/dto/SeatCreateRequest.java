package com.ticket.concert.dto;

import lombok.Getter;

@Getter
public class SeatCreateRequest {
    private Long venueId;
    private Long seatGradeId;
    private String seatNumber;
}
