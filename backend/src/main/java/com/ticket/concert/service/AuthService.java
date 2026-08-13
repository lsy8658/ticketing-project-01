package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.UserRole;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Long signUp(String email, String password, String nickname) {
        userRepository.findByEmail(email)
                .ifPresent(m -> {throw new RuntimeException("이미 가입된 이메일 입니다.");});

        User user = User.builder()
                .email(email)
                .nickname(nickname)
                .password(passwordEncoder.encode(password))
                .role(UserRole.USER)
                .build();
        return userRepository.save(user).getId();
    }

    public User login (String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        return user;
    }
}
