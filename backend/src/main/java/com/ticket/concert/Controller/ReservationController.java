package com.ticket.concert.Controller;


import com.ticket.concert.domain.Reservation;
import com.ticket.concert.dto.ReservationCreateRequest;
import com.ticket.concert.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @PostMapping
    public ResponseEntity<Long> create(
            Authentication authentication,
            @Valid @RequestBody ReservationCreateRequest request
            ) {
        Long userId = (Long) authentication.getPrincipal();

        Long reservationId = reservationService.create(
                userId,
                request.getConcertScheduleId(),
                request.getScheduleSeatIds()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(reservationId);
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> cancel(
            Authentication authentication,
            @PathVariable("reservationId") Long reservationId
    ) {
        Long userId = (Long) authentication.getPrincipal();
        reservationService.cancelReservation(reservationId,userId);

        return ResponseEntity.noContent().build();
    }
}
