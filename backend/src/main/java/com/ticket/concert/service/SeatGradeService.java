package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.SeatGrade;
import com.ticket.concert.dto.SeatGradeResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.ConcertRepository;
import com.ticket.concert.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatGradeService {
    private final SeatGradeRepository seatGradeRepository;
    private final ConcertRepository concertRepository;

    public SeatGradeResponse create(Long concertId, String name, Long price) {
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

            SeatGrade seatGrade = new SeatGrade(concert, name, price);

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);

        return new SeatGradeResponse(
                savedSeatGrade.getId(),
                savedSeatGrade.getName(),
                savedSeatGrade.getPrice());
    }
    public List<SeatGradeResponse> findAllByConcert(Long concertId) {
        List<SeatGrade> seatGrades = seatGradeRepository.findAllByConcertId(concertId);

        return seatGrades.stream()
                .map(sg -> new SeatGradeResponse(sg.getId(), sg.getName(), sg.getPrice())).toList();
    }
}
