package com.ticket.concert.Controller;

import com.ticket.concert.config.JwtProvider;
import com.ticket.concert.domain.User;
import com.ticket.concert.dto.LoginRequest;
import com.ticket.concert.dto.SignupRequest;
import com.ticket.concert.service.AuthService;
import com.ticket.concert.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody SignupRequest request) {
        Long id = authService.signUp(request.getEmail(), request.getPassword(), request.getNickname());
        return ResponseEntity.ok(id);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User user = authService.login(request.getEmail(), request.getPassword());
        String token = jwtProvider.createToken(user.getId(), user.getEmail(), user.getRole());
        return ResponseEntity.ok(token);
    }
}