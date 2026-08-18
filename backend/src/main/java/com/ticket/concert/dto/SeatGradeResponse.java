package com.ticket.concert.dto;

import lombok.Getter;

@Getter
public class SeatGradeResponse {

    private Long id;
    private String name;
    private Long price;

    public SeatGradeResponse(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}