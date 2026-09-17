package com.ticket.concert.Controller;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.dto.ConcertScheduleCreateRequest;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.dto.ConcertScheduleUpdateRequest;
import com.ticket.concert.service.ConcertScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concert-schedules")
@RequiredArgsConstructor
public class ConcertScheduleController {
    private final ConcertScheduleService concertScheduleService;

    @PostMapping
    public ResponseEntity<ConcertScheduleResponse> createConcertSchedule(
            Authentication authentication,
            @Valid @RequestBody ConcertScheduleCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        ConcertScheduleResponse schedule = concertScheduleService.create(
                userId, request.getConcertId(), request.getVenueId(), request.getStartAt(), request.getEndAt()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(schedule);
    }

    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<List<ConcertSchedule>> getConcertSchedules (@PathVariable("concertId") Long concertId) {
        List<ConcertSchedule> concertSchedules = concertScheduleService.getConcertSchedules(concertId);
        return ResponseEntity.ok(concertSchedules);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ConcertScheduleResponse> updateConcertSchedule(
            Authentication authentication,
            @PathVariable("scheduleId") Long scheduleId,
            @RequestBody ConcertScheduleUpdateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        ConcertScheduleResponse response = concertScheduleService.update(scheduleId, userId, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteConcertSchedule(
            Authentication authentication,
            @PathVariable("scheduleId") Long scheduleId
    ) {
        Long userId = (Long) authentication.getPrincipal();

        concertScheduleService.delete(scheduleId, userId);
        return ResponseEntity.noContent().build();
    }
}

