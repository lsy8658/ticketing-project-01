package com.ticket.concert.Controller;

import com.ticket.concert.dto.SeatGradeCreateRequest;
import com.ticket.concert.dto.SeatGradeResponse;
import com.ticket.concert.service.SeatGradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                request.getName(),
                request.getPrice()
        );

        return ResponseEntity.ok(response);
    }
}