package com.ticket.concert.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class ConcertScheduleUpdateRequest {
    @NotNull(message = "시작일은 필수입니다.")
    private LocalDateTime startAt;

    @NotNull(message = "종료일은 필수입니다.")
    private LocalDateTime endAt;

    public ConcertScheduleUpdateRequest (LocalDateTime startAt, LocalDateTime endAt) {
        this.startAt = startAt;
        this.endAt = endAt;
    }
}
