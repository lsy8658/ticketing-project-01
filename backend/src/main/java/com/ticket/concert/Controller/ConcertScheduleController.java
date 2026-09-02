package com.ticket.concert.Controller;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.dto.ConcertScheduleCreateRequest;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.dto.ConcertScheduleUpdateRequest;
import com.ticket.concert.service.ConcertScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/concert-schedules")
@RequiredArgsConstructor
public class ConcertScheduleController {
    private final ConcertScheduleService concertScheduleService;

    @PostMapping
    public ResponseEntity<ConcertScheduleResponse> createConcertSchedule(@RequestBody ConcertScheduleCreateRequest request) {
        ConcertScheduleResponse schedule = concertScheduleService.create(
                request.getConcertId() , request.getVenueId(), request.getStartAt()
        );
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<List<ConcertSchedule>> getConcertSchedules (@PathVariable("concertId") Long concertId) {
        List<ConcertSchedule> concertSchedules = concertScheduleService.getConcertSchedules(concertId);
        return ResponseEntity.ok(concertSchedules);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ConcertScheduleResponse> updateConcertSchedule(
            @PathVariable("scheduleId") Long scheduleId,
            @RequestBody ConcertScheduleUpdateRequest request
    ) {
        ConcertScheduleResponse concertScheduleResponse = concertScheduleService.update(scheduleId, request);
        return ResponseEntity.ok(concertScheduleResponse);
    }
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteConcertSchedule(@PathVariable("scheduleId") Long scheduleId) {
        concertScheduleService.delete(scheduleId);
        return ResponseEntity.noContent().build();
    }
}

