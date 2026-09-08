package com.ticket.concert.Controller;

import com.ticket.concert.dto.SeatGradeCreateRequest;
import com.ticket.concert.dto.SeatGradeResponse;
import com.ticket.concert.service.SeatGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/seat-grades")
public class SeatGradeController {

    private final SeatGradeService seatGradeService;

    @PostMapping
    public ResponseEntity<SeatGradeResponse> create(
            @Valid @RequestBody SeatGradeCreateRequest request
    ) {
        SeatGradeResponse response = seatGradeService.create(
                request.getConcertId(),
                request.getName(),
                request.getPrice()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/concerts/{concertId}")
    public ResponseEntity<List<SeatGradeResponse>> getByConcert(
            @PathVariable("concertId") Long concertId
    ) {
        List<SeatGradeResponse> response = seatGradeService.findAllByConcert(concertId);
        return ResponseEntity.ok(response);
    }
}