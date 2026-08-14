package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.dto.UserResponse;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse findById(Long userId) {
         User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
         return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }

}
