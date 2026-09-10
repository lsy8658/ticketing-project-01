package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole requestedRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleRequestStatus status;

    public RoleRequest(User user, UserRole requestedRole) {
        this.user = user;
        this.requestedRole = requestedRole;
        this.status = RoleRequestStatus.PENDING;
    }

    public void approve() {
        this.status = RoleRequestStatus.APPROVED;
    }

    public void reject() {
        this.status = RoleRequestStatus.REJECTED;
    }
}
