package com.ticket.concert.service;

import com.ticket.concert.domain.SeatGrade;
import com.ticket.concert.dto.SeatGradeResponse;
import com.ticket.concert.repository.SeatGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatGradeService {
    private final SeatGradeRepository seatGradeRepository;

    public SeatGradeResponse create(String name, Long price) {
        SeatGrade seatGrade = new SeatGrade(name, price);

        SeatGrade savedSeatGrade = seatGradeRepository.save(seatGrade);

        return new SeatGradeResponse(
                savedSeatGrade.getId(),
                savedSeatGrade.getName(),
                savedSeatGrade.getPrice());
    }
}
