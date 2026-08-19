package com.ticket.concert.service;

import com.ticket.concert.domain.Seat;
import com.ticket.concert.domain.SeatGrade;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.SeatResponse;
import com.ticket.concert.repository.SeatGradeRepository;
import com.ticket.concert.repository.SeatRepository;
import com.ticket.concert.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final VenueRepository venueRepository;

    public SeatResponse create(
            Long venueId,
            Long seatGradeId,
            String seatNumber
    ) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new RuntimeException("공연장을 찾을 수 없습니다."));

        SeatGrade seatGrade = seatGradeRepository.findById(seatGradeId)
                .orElseThrow(() -> new RuntimeException("좌석 등급을 찾을 수 없습니다."));

        Seat seat = new Seat(
                venue,
                seatGrade,
                seatNumber
        );
        Boolean exists = seatRepository.existsByVenueAndSeatNumber(venue, seatNumber);

        if (exists) {
            throw new RuntimeException("이미 존재하는 좌석입니다.");
        }
        Seat savedSeat = seatRepository.save(seat);

        return new SeatResponse(
                savedSeat.getId(),
                savedSeat.getSeatNumber(),
                savedSeat.getSeatGrade().getId()
        );
    }
}