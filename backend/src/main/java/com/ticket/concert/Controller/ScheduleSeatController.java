package com.ticket.concert.Controller;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.service.ScheduleSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule-seat")
public class ScheduleSeatController {
    private final ScheduleSeatService scheduleSeatService;

    @PostMapping("/{concertScheduleId}")
    public ResponseEntity<Void> create(@PathVariable("concertScheduleId") Long concertScheduleId) {
        scheduleSeatService.create(concertScheduleId);
        return ResponseEntity.ok().build();
    }
}
