package com.ticket.concert.service;

import com.ticket.concert.domain.Seat;
import com.ticket.concert.domain.SeatGrade;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.SeatResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        SeatGrade seatGrade = seatGradeRepository.findById(seatGradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEAT_GRADE_NOT_FOUND));

        Seat seat = new Seat(
                venue,
                seatGrade,
                seatNumber
        );
        Boolean exists = seatRepository.existsByVenueAndSeatNumber(venue, seatNumber);

        if (exists) {
            throw new CustomException(ErrorCode.SEAT_ALREADY_EXISTS);
        }
        Seat savedSeat = seatRepository.save(seat);

        return new SeatResponse(
                savedSeat.getId(),
                savedSeat.getSeatNumber(),
                savedSeat.getSeatGrade().getId()
        );
    }
}