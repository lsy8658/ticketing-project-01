package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

@Getter
public class VenueCreateRequest {
    @NotBlank(message = "공연장 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    @NotNull(message = "수용인원은 필수입니다.")
    @Positive(message = "수용인원은 1 이상이어야 합니다.")
    private Integer capacity;

    @NotBlank(message = "담당자 연락처는 필수입니다.")
    private String managerPhone;
}