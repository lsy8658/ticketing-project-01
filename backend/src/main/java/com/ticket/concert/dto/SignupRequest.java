package com.ticket.concert.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class SignupRequest {

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*\\d).{8,}$",
            message = "비밀번호는 영문+숫자 포함 8자 이상이어야 합니다."
    )
    private String password;

    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}