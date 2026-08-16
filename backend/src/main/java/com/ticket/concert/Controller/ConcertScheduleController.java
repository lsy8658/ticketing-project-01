package com.ticket.concert.Controller;

import com.ticket.concert.dto.ConcertScheduleCreateRequest;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.service.ConcertScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

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
}
