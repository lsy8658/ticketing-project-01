package com.ticket.concert.Controller;

import com.ticket.concert.dto.SeatGradeCreateRequest;
import com.ticket.concert.dto.SeatGradeResponse;
import com.ticket.concert.service.SeatGradeService;
import lombok.RequiredArgsConstructor;
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
            @RequestBody SeatGradeCreateRequest request
    ) {
        SeatGradeResponse response = seatGradeService.create(
                request.getConcertId(),
                request.getName(),
                request.getPrice()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/concerts/{concertId}")
    public ResponseEntity<List<SeatGradeResponse>> getByConcert (
            @PathVariable("concertId") Long concertId
    ) {
        List<SeatGradeResponse> response = seatGradeService.findAllByConcert(concertId);
        return ResponseEntity.ok(response);
    }
}