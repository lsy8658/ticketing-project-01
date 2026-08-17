package com.ticket.concert.Controller;


import com.ticket.concert.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ResponseEntity<Long> create(
           Authentication authentication,
           @RequestParam Long concertScheduleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        Long reservationId = reservationService.create(
                userId,
                concertScheduleId
        );

        return ResponseEntity.ok(reservationId);
    }
}
