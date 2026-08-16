package com.ticket.concert.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ConcertScheduleCreateRequest {
    private Long concertId;
    private Long venueId;
    private LocalDateTime startAt;
}
