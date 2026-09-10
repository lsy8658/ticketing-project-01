package com.ticket.concert.dto;

import com.ticket.concert.domain.RoleRequest;
import com.ticket.concert.domain.RoleRequestStatus;
import com.ticket.concert.domain.UserRole;
import lombok.Getter;

@Getter
public class RoleRequestResponse {

    private Long id;
    private Long userId;
    private String nickname;
    private String email;
    private UserRole requestedRole;
    private RoleRequestStatus status;

    public RoleRequestResponse(Long id, Long userId, String nickname, String email,
                               UserRole requestedRole, RoleRequestStatus status) {
        this.id = id;
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.requestedRole = requestedRole;
        this.status = status;
    }

    public static RoleRequestResponse from(RoleRequest roleRequest) {
        return new RoleRequestResponse(
                roleRequest.getId(),
                roleRequest.getUser().getId(),
                roleRequest.getUser().getNickname(),
                roleRequest.getUser().getEmail(),
                roleRequest.getRequestedRole(),
                roleRequest.getStatus()
        );
    }
}