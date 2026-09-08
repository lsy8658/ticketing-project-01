package com.ticket.concert.Controller;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.dto.ScheduleSeatResponse;
import com.ticket.concert.service.ScheduleSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/schedule-seat")
public class ScheduleSeatController {
    private final ScheduleSeatService scheduleSeatService;

    @GetMapping("/{concertScheduleId}")
    public ResponseEntity<List<ScheduleSeatResponse>> getSeats(
            @PathVariable("concertScheduleId") Long concertScheduleId
    ) {
        List<ScheduleSeatResponse> response = scheduleSeatService.findAllByConcertSchedule(concertScheduleId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{concertScheduleId}")
    public ResponseEntity<Void> create(@PathVariable("concertScheduleId") Long concertScheduleId) {
        scheduleSeatService.create(concertScheduleId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
