package com.ticket.concert.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ConcertRegisterRequest {
    @NotBlank(message = "제목은 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String description;

    @NotBlank(message = "이미지 url을 입력해주세요.")
    private String imageUrl;

    @NotNull(message = "판매 시작일은 필수입니다.")
    private LocalDateTime salesStartAt;

    @NotNull(message = "판매 마감일은 필수입니다.")
    private LocalDateTime salesEndAt;

    private List<ImageInfo> images;

    @NotEmpty(message = "회차를 1개 이상 입력해주세요.")
    @Valid
    private List<ScheduleInfo> schedules;

    @NotEmpty(message = "좌석 등급을 1개 이상 입력해주세요.")
    @Valid
    private List<SeatGradeInfo> seatGrades;

    @NotEmpty(message = "구역 배정을 1개 이상 입력해주세요.")
    @Valid
    private List<RowAssignInfo> rowAssigns;

    @Getter
    public static class ScheduleInfo {
        @NotNull(message = "공연장 ID는 필수입니다.")
        private Long venueId;

        @NotNull(message = "공연 시작일시는 필수입니다.")
        private LocalDateTime startAt;

        @NotNull(message = "공연 종료일시는 필수입니다.")
        private LocalDateTime endAt;
    }

    @Getter
    public static class SeatGradeInfo {
        @NotBlank(message = "등급명은 필수입니다.")
        private String name;

        @NotNull(message = "가격은 필수입니다.")
        private Long price;
    }

    @Getter
    public static class RowAssignInfo {
        @NotBlank(message = "구역명은 필수입니다.")
        private String rowName;

        @NotBlank(message = "등급명은 필수입니다.")
        private String gradeName;
    }
}