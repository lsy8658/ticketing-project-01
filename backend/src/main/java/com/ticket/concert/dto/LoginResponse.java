package com.ticket.concert.dto;

import com.ticket.concert.domain.UserRole;
import lombok.Getter;

@Getter
public class LoginResponse {
    private String token;
    private Long userId;
    private String email;
    private String nickname;
    private UserRole role;

    public LoginResponse(String token, Long userId, String email, String nickname, UserRole role) {
        this.token = token;
        this.userId = userId;
        this.email = email;
        this.nickname = nickname;
        this.role = role;
    }
}