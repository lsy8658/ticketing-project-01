package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.dto.UserResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse findById(Long userId) {
         User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
         return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }
}
