package com.ticket.concert.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ConcertScheduleUpdateRequest {
    private LocalDateTime startAt;

    public ConcertScheduleUpdateRequest (LocalDateTime startAt) {
        this.startAt = startAt;
    }
}
