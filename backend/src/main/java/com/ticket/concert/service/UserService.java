package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.User;
import com.ticket.concert.domain.UserRole;
import com.ticket.concert.dto.UserResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.ConcertRepository;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;
    private final ConcertService concertService;
    private final TransactionTemplate transactionTemplate;

    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return new UserResponse(user.getId(), user.getEmail(), user.getNickname(), user.getRole());
    }

    public void updateRole(Long userId, UserRole role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == UserRole.MANAGER && role == UserRole.USER) {
            List<Concert> concerts = concertRepository.findAllByCreateBy(user);
            for (Concert concert : concerts) {
                concertService.delete(userId, concert.getId());
            }
        }

        transactionTemplate.executeWithoutResult(status ->
                userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                        .changeRole(role));
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getNickname(),
                        user.getRole()
                ))
                .toList();
    }
}