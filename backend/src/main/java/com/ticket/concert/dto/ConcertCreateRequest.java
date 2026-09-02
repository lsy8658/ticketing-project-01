package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

@Getter
public class ConcertCreateRequest {
    @NotBlank(message = "제목은 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String description;

    @NotBlank(message = "이미지 url을 입력해주세요.")
    private String imageUrl;
    private List<ImageInfo> images;
}
