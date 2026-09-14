package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ConcertSeatGradeRequest {
    @NotBlank(message = "구역명은 필수입니다.")
    private String rowName;

    @NotNull(message = "등급 ID는 필수입니다.")
    private Long seatGradeId;
}
