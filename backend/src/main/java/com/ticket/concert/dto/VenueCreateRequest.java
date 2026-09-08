package com.ticket.concert.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VenueCreateRequest {
    @NotBlank(message = "공연장 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;
}
