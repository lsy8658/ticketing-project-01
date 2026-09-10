package com.ticket.concert.repository;

import com.ticket.concert.domain.RoleRequest;
import com.ticket.concert.domain.RoleRequestStatus;
import com.ticket.concert.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRequestRepository extends JpaRepository<RoleRequest,Long> {
    List<RoleRequest> findAllByStatus(RoleRequestStatus status);
    boolean existsByUserAndStatus(User user, RoleRequestStatus status);
}
