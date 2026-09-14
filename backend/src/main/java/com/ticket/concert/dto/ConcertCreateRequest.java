package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ConcertCreateRequest {
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
}