package com.ticket.concert.Controller;

import com.ticket.concert.domain.User;
import com.ticket.concert.dto.RoleUpdateRequest;
import com.ticket.concert.dto.UserResponse;
import com.ticket.concert.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse user = userService.findById(userId);

        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<Void> updateRole(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody RoleUpdateRequest request
            ) {
        userService.updateRole(userId, request.getRole());
        return ResponseEntity.noContent().build();
    }
}