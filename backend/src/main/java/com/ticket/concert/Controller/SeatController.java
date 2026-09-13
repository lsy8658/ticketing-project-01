package com.ticket.concert.Controller;

import com.ticket.concert.dto.SeatBulkCreateRequest;
import com.ticket.concert.dto.SeatCreateRequest;
import com.ticket.concert.dto.SeatResponse;
import com.ticket.concert.service.SeatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seats")
public class SeatController {
    private final SeatService seatService;

    @PostMapping
    public ResponseEntity<List<SeatResponse>> create(@Valid @RequestBody SeatBulkCreateRequest request) {
        List<SeatResponse> seats = seatService.createBulk(request.getVenueId(), request.getRows());
        return ResponseEntity.status(HttpStatus.CREATED).body(seats);
    }
}