package com.ticket.concert.Controller;

import com.ticket.concert.dto.ConcertRegisterRequest;
import com.ticket.concert.service.ConcertRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts/register")
public class ConcertRegisterController {
    private final ConcertRegisterService concertRegisterService;

    @PostMapping
    public ResponseEntity<Long> register(
            Authentication authentication,
            @Valid @RequestBody ConcertRegisterRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long concertId = concertRegisterService.register(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(concertId);
    }
}
