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
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public ConcertScheduleResponse create(Long userId, Long concertId, Long venueId, LocalDateTime startAt, LocalDateTime endAt) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        if (!concert.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        if (!venue.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (!startAt.isBefore(endAt)) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_PERIOD_INVALID);
        }

        if (concertScheduleRepository.existsOverlappingSchedule(venue, startAt.toLocalDate(), endAt.toLocalDate())) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_ALREADY_EXISTS);
        }

        ConcertSchedule schedule = new ConcertSchedule(concert, venue, startAt, endAt);
        ConcertSchedule savedSchedule = concertScheduleRepository.save(schedule);

        return ConcertScheduleResponse.from(savedSchedule);
    }

    public List<ConcertScheduleResponse> getConcertSchedules(Long concertId) {
        return concertScheduleRepository.findAllByConcertId(concertId).stream()
                .map(ConcertScheduleResponse::from)
                .toList();
    }

    @Transactional
    public ConcertScheduleResponse update(Long scheduleId, Long userId,ConcertScheduleUpdateRequest request) {
        ConcertSchedule schedule = concertScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        if (!schedule.getConcert().getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        LocalDateTime startAt = request.getStartAt();
        LocalDateTime endAt = request.getEndAt();

        if (!startAt.isBefore(endAt)) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_PERIOD_INVALID);
        }

        if (reservationRepository.existsByConcertScheduleAndStatus(schedule, ReservationStatus.RESERVED)) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_HAS_RESERVATION);
        }

        if (concertScheduleRepository.existsOverlappingScheduleExcludingSelf(
                schedule.getVenue(), scheduleId, startAt.toLocalDate(), endAt.toLocalDate())) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_ALREADY_EXISTS);
        }

        schedule.updateStartAt(request.getStartAt());
        schedule.updateEndAt(request.getEndAt());

        return ConcertScheduleResponse.from(schedule);
    }

    @Transactional
    public void delete(Long scheduleId, Long userId) {
        ConcertSchedule schedule = concertScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        if (!schedule.getConcert().getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (reservationRepository.existsByConcertScheduleAndStatus(schedule, ReservationStatus.RESERVED)) {
            throw new CustomException(ErrorCode.CONCERT_SCHEDULE_HAS_RESERVATION);
        }

        scheduleSeatRepository.deleteAllByConcertSchedule(schedule);
        concertScheduleRepository.delete(schedule);
    }
}
