package com.ticket.concert.service;


import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.repository.ConcertRepository;
import com.ticket.concert.repository.ConcertScheduleRepository;
import com.ticket.concert.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ConcertRepository concertRepository;
    private final VenueRepository venueRepository;

    public ConcertScheduleResponse create(Long concertId, Long venueId, LocalDateTime startAt) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다."));
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다."));

        ConcertSchedule schedule = new ConcertSchedule(concert, venue, startAt);
        schedule = concertScheduleRepository.save(schedule);
        return ConcertScheduleResponse.from(schedule);
    }
}
