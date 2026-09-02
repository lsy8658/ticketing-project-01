package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.UserRole;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(String email, String password, String nickname, UserRole role) {
        userRepository.findByEmail(email)
                .ifPresent(m -> {throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);});

        if (role == UserRole.ADMIN) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }


        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userRepository.save(user).getId();
    }

    public User login (String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }
        return user;
    }
}
