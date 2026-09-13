package com.ticket.concert.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.util.List;

@Getter
public class SeatBulkCreateRequest {

    @NotNull(message = "공연장 ID는 필수입니다.")
    private Long venueId;

    @NotEmpty(message = "행을 1개 이상 입력해주세요.")
    @Valid
    private List<RowRequest> rows;

    @Getter
    public static class RowRequest {
        @NotBlank(message = "행 이름은 필수입니다.")
        private String rowName;

        @NotNull(message = "좌석 수는 필수입니다.")
        @Positive(message = "좌석 수는 1 이상이어야 합니다.")
        private Integer seatCount;
    }
}