package com.ticket.concert.service;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.domain.Seat;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.ConcertScheduleRepository;
import com.ticket.concert.repository.ScheduleSeatRepository;
import com.ticket.concert.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleSeatService {
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public void create(Long concertScheduleId) {
        ConcertSchedule schedule = concertScheduleRepository.findById(concertScheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        if (scheduleSeatRepository.existsByConcertSchedule(schedule)) {
            throw new CustomException(ErrorCode.SCHEDULE_SEAT_ALREADY_EXISTS);
        }
        // 공연장소에 대한 좌석들
        List<Seat> seats = seatRepository.findAllByVenue(schedule.getVenue());

        List<ScheduleSeat> scheduleSeats = seats.stream()
                .map(seat -> new ScheduleSeat(schedule, seat))
                .toList();
        System.out.println("좌석 개수 :" + seats.size());
        scheduleSeatRepository.saveAll(scheduleSeats);
    }
}
