package com.ticket.concert.dto;

import com.ticket.concert.domain.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoleUpdateRequest {

    @NotNull(message = "변경할 권한은 필수입니다.")
    private UserRole role;
}