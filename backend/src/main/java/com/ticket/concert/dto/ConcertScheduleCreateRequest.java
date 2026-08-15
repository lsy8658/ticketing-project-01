package com.ticket.concert.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ConcertScheduleCreateRequest {
    private Long concerId;
    private Long venueId;
    private LocalDateTime startAt;
}
