package com.ticket.concert.dto;

import com.ticket.concert.domain.UserRole;
import lombok.Getter;

@Getter
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private UserRole role;

    public UserResponse (Long id, String email, String nickname, UserRole role) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
}
