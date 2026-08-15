package com.ticket.concert.Controller;

import com.ticket.concert.service.ConcertScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/concert-schedules")
@RequiredArgsConstructor
public class ConcertScheduleController {
    private final ConcertScheduleService concertScheduleService;

//    @PostMapping
//    public ResponseEntity<?>
}
