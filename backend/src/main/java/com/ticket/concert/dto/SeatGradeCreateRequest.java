package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SeatGradeCreateRequest {
    @NotNull(message = "콘서트 ID는 필수입니다.")
    private Long concertId;

    @NotBlank(message = "등급명은 필수입니다.")
    private String name;

    @NotNull(message = "가격은 필수입니다.")
    private Long price;
}


