package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ConcertSeatGradeStatusResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConcertSeatGradeService {
    private final ConcertSeatGradeRepository concertSeatGradeRepository;
    private final ConcertRepository concertRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    public void assign(Long concertId, Long userId, String rowName, Long seatGradeId) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        if (!concert.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        SeatGrade seatGrade = seatGradeRepository.findById(seatGradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_GRADE_NOT_FOUND));

        if (!seatGrade.getConcert().getId().equals(concertId)) {
            throw new CustomException(ErrorCode.SEAT_GRADE_NOT_FOUND);
        }

        Optional<ConcertSeatGrade> existing = concertSeatGradeRepository.findByConcertAndRowName(concert, rowName);

        if (existing.isPresent()) {
            existing.get().updateSeatGrade(seatGrade);
        } else {
            concertSeatGradeRepository.save(new ConcertSeatGrade(concert, rowName, seatGrade));
        }
    }

    public List<ConcertSeatGradeStatusResponse> getStatus(Long concertId) {
        ConcertSchedule schedule = concertScheduleRepository.findFirstByConcertId(concertId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        List<Seat> seats = seatRepository.findAllByVenue(schedule.getVenue());
        List<String> rowNames = seats.stream()
                .map(Seat::getRowName)
                .distinct()
                .toList();
        List<ConcertSeatGrade> assigned = concertSeatGradeRepository.findAllByConcert(schedule.getConcert());

        return rowNames.stream()
                .map(rowName -> {
                    String gradeName = assigned.stream()
                            .filter(a -> a.getRowName().equals(rowName))
                            .map(a -> a.getSeatGrade().getName())
                            .findFirst()
                            .orElse(null);
                    return new ConcertSeatGradeStatusResponse(rowName, gradeName);
                }).toList();

    }

}
