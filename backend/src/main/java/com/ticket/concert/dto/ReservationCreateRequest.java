package com.ticket.concert.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class ReservationCreateRequest {
    @NotNull(message = "공연 회차는 필수입니다.")
    private Long concertScheduleId;

    @NotEmpty(message = "예약할 좌석을1개 이상 선택해주세요.")
    private List<Long> scheduleSeatIds;
}
