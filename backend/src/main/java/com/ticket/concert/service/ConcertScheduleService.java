package com.ticket.concert.service;


import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertScheduleService {
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ConcertRepository concertRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;

    public ConcertScheduleResponse create(Long concertId, Long venueId, LocalDateTime startAt) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다."));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다."));

        ConcertSchedule schedule = new ConcertSchedule(concert, venue, startAt);

        ConcertSchedule savedSchedule = concertScheduleRepository.save(schedule);

        List<Seat> seats = seatRepository.findAllByVenue(venue);

        List<ScheduleSeat> scheduleSeats = seats.stream()
                .map(seat -> new ScheduleSeat(savedSchedule, seat)).toList();

        scheduleSeatRepository.saveAll(scheduleSeats);

        return ConcertScheduleResponse.from(savedSchedule);
    }
}
