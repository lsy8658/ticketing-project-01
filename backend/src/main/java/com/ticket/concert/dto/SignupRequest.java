package com.ticket.concert.dto;

import com.ticket.concert.domain.UserRole;
import lombok.Getter;


@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String nickname;
    private UserRole role;
}
