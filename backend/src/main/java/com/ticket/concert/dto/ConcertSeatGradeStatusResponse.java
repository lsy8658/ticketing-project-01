package com.ticket.concert.dto;

import lombok.Getter;

@Getter
public class ConcertSeatGradeStatusResponse {
    private String rowName;
    private String seatGradeName;

    public ConcertSeatGradeStatusResponse(String rowName, String seatGradeName) {
        this.rowName = rowName;
        this.seatGradeName = seatGradeName;
    }
}
