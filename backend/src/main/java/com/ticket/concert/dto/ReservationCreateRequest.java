package com.ticket.concert.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReservationCreateRequest {
    private Long concertScheduleId;
    private List<Long> scheduleSeatIds;
}
