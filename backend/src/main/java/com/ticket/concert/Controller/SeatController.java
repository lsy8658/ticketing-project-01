package com.ticket.concert.Controller;

import com.ticket.concert.dto.SeatCreateRequest;
import com.ticket.concert.dto.SeatResponse;
import com.ticket.concert.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats")
public class SeatController {
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<SeatResponse> create(@RequestBody SeatCreateRequest request) {
        SeatResponse seat = seatService.create(request.getVenueId(), request.getSeatGradeId(), request.getSeatNumber());
        return ResponseEntity.ok(seat);
    }

}
