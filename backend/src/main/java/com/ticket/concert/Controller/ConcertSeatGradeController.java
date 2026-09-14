package com.ticket.concert.Controller;

import com.ticket.concert.dto.ConcertSeatGradeRequest;
import com.ticket.concert.dto.ConcertSeatGradeStatusResponse;
import com.ticket.concert.service.ConcertSeatGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concert-seat-grades")
public class ConcertSeatGradeController {
    private final ConcertSeatGradeService concertSeatGradeService;

    @PostMapping("/{concertId}")
    public ResponseEntity<Void> assign(
            Authentication authentication,
            @PathVariable("concertId") Long concertId,
            @Valid @RequestBody ConcertSeatGradeRequest request
            ) {
        Long userId = (Long) authentication.getPrincipal();
        concertSeatGradeService.assign(
                concertId,
                userId,
                request.getRowName(),
                request.getSeatGradeId()
        );
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{concertId}")
    public ResponseEntity<List<ConcertSeatGradeStatusResponse>> getStatus(
            @PathVariable("concertId") Long concertId
    ) {
        return ResponseEntity.ok(concertSeatGradeService.getStatus(concertId));
    }

}
