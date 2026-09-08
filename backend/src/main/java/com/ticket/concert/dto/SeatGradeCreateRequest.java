package com.ticket.concert.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatGradeCreateRequest {
    private Long concertId;
    private String name;
    private Long price;
}


