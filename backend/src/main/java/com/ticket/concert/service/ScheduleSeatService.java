package com.ticket.concert.service;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Seat;
import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.domain.SeatGrade;
import com.ticket.concert.dto.ScheduleSeatResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.ConcertScheduleRepository;
import com.ticket.concert.repository.ScheduleSeatRepository;
import com.ticket.concert.repository.SeatGradeRepository;
import com.ticket.concert.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleSeatService {
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;
    private final SeatGradeRepository seatGradeRepository;

    public void create(Long concertScheduleId, Long seatGradeId, List<Long> seatIds) {
        ConcertSchedule schedule = concertScheduleRepository.findById(concertScheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        SeatGrade seatGrade = seatGradeRepository.findById(seatGradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_GRADE_NOT_FOUND));

        if (scheduleSeatRepository.existsByConcertScheduleAndSeatIdIn(schedule, seatIds)) {
            throw new CustomException(ErrorCode.SCHEDULE_SEAT_ALREADY_EXISTS);
        }

        List<Seat> seats = seatRepository.findAllById(seatIds);

        List<ScheduleSeat> scheduleSeats = seats.stream()
                .map(seat -> new ScheduleSeat(schedule, seat, seatGrade))
                .toList();

        scheduleSeatRepository.saveAll(scheduleSeats);
    }

    public List<ScheduleSeatResponse> findAllByConcertSchedule(Long concertScheduleId) {
        List<ScheduleSeat> scheduleSeats = scheduleSeatRepository.findAllByConcertScheduleId(concertScheduleId);
        return scheduleSeats.stream()
                .map(ScheduleSeatResponse::from)
                .toList();
    }
}