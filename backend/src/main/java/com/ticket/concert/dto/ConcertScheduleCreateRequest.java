package com.ticket.concert.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ConcertScheduleCreateRequest {
    @NotNull(message = "콘서트 ID는 필수입니다.")
    private Long concertId;

    @NotNull(message = "공연장 ID는 필수입니다.")
    private Long venueId;

    @NotNull(message = "공연 일시는 필수입니다.")
    private LocalDateTime startAt;
}