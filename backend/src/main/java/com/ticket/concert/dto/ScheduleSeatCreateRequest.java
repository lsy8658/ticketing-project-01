package com.ticket.concert.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class ScheduleSeatCreateRequest {

    @NotNull(message = "좌석 등급 ID는 필수입니다.")
    private Long seatGradeId;

    @NotEmpty(message = "좌석을 1개 이상 선택해주세요.")
    private List<Long> seatIds;
}