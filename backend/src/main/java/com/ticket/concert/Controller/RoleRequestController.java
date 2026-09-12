package com.ticket.concert.Controller;

import com.ticket.concert.dto.RoleRequestCreateRequest;
import com.ticket.concert.dto.RoleRequestResponse;
import com.ticket.concert.service.RoleRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/role-requests")
public class RoleRequestController {
    private final RoleRequestService roleRequestService;

    @PostMapping
    public ResponseEntity<Long> create(
            Authentication authentication,
            @Valid @RequestBody RoleRequestCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long id = roleRequestService.create(userId, request.getRequestedRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping
    public ResponseEntity<List<RoleRequestResponse>> getPending() {
        return ResponseEntity.ok(roleRequestService.getPending());
    }

    @GetMapping("/me")
    public ResponseEntity<RoleRequestResponse> getMyRequest(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return roleRequestService.findLatestByUser(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable("id") Long id) {
        roleRequestService.approve(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<Void> reject(@PathVariable("id") Long id) {
        roleRequestService.reject(id);
        return ResponseEntity.noContent().build();
    }
}