package com.ticket.concert.service;


import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ConcertScheduleResponse;
import com.ticket.concert.dto.ConcertScheduleUpdateRequest;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        if (concertScheduleRepository.existsByVenueAndStartAt(venue, startAt)) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_ALREADY_EXISTS);
        }

        ConcertSchedule schedule = new ConcertSchedule(concert, venue, startAt);

        ConcertSchedule savedSchedule = concertScheduleRepository.save(schedule);

        List<Seat> seats = seatRepository.findAllByVenue(venue);

        List<ScheduleSeat> scheduleSeats = seats.stream()
                .map(seat -> new ScheduleSeat(savedSchedule, seat)).toList();

        scheduleSeatRepository.saveAll(scheduleSeats);

        return ConcertScheduleResponse.from(savedSchedule);
    }

    public List<ConcertSchedule> getConcertSchedules(Long concertId) {
        return concertScheduleRepository.findAllByConcertId(concertId);
    }

    @Transactional
    public ConcertScheduleResponse update(Long scheduleId, ConcertScheduleUpdateRequest request) {
        ConcertSchedule schedule = concertScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        schedule.updateStartAt(request.getStartAt());

        return ConcertScheduleResponse.from(schedule);
    }
}
