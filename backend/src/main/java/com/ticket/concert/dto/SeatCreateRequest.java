package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SeatCreateRequest {

    @NotNull(message = "공연장 ID는 필수입니다.")
    private Long venueId;

    @NotBlank(message = "좌석 번호는 필수입니다.")
    private String seatNumber;
}