package com.ticket.concert.service;

import com.ticket.concert.domain.RoleRequest;
import com.ticket.concert.domain.RoleRequestStatus;
import com.ticket.concert.domain.User;
import com.ticket.concert.domain.UserRole;
import com.ticket.concert.dto.RoleRequestResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.RoleRequestRepository;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleRequestService {
    private final RoleRequestRepository roleRequestRepository;
    private final UserRepository userRepository;

    public Long create(Long userId, UserRole requestedRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (requestedRole == UserRole.ADMIN) {
            throw new CustomException(ErrorCode.ROLE_REQUEST_INVALID);
        }

        if (user.getRole() == requestedRole) {
            throw new CustomException(ErrorCode.ROLE_REQUEST_INVALID);
        }

        if (roleRequestRepository.existsByUserAndStatus(user, RoleRequestStatus.PENDING)) {
            throw new CustomException(ErrorCode.ROLE_REQUEST_ALREADY_PENDING);
        }

        RoleRequest roleRequest = new RoleRequest(user, requestedRole);
        return roleRequestRepository.save(roleRequest).getId();
    }

    @Transactional(readOnly = true)
    public List<RoleRequestResponse> getPending() {
        return roleRequestRepository.findAllByStatus(RoleRequestStatus.PENDING)
                .stream()
                .map(RoleRequestResponse::from)
                .toList();
    }

    @Transactional
    public void approve(Long roleRequestId) {
        RoleRequest roleRequest = roleRequestRepository.findById(roleRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_REQUEST_NOT_FOUND));

        if (roleRequest.getStatus() != RoleRequestStatus.PENDING) {
            throw new CustomException(ErrorCode.ROLE_REQUEST_ALREADY_PROCESSED);
        }

        roleRequest.approve();
        roleRequest.getUser().changeRole(roleRequest.getRequestedRole());
    }

    @Transactional
    public void reject(Long roleRequestId) {
        RoleRequest roleRequest = roleRequestRepository.findById(roleRequestId)
                .orElseThrow(() -> new CustomException(ErrorCode.ROLE_REQUEST_NOT_FOUND));

        if (roleRequest.getStatus() != RoleRequestStatus.PENDING) {
            throw new CustomException(ErrorCode.ROLE_REQUEST_ALREADY_PROCESSED);
        }

        roleRequest.reject();
    }
}
