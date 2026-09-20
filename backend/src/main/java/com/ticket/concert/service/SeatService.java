package com.ticket.concert.service;

import com.ticket.concert.domain.Seat;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.SeatBulkCreateRequest;
import com.ticket.concert.dto.SeatResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.SeatRepository;
import com.ticket.concert.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final VenueRepository venueRepository;

    public List<SeatResponse> createBulk(Long userId, Long venueId, List<SeatBulkCreateRequest.RowRequest> rows) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));

        if (!venue.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        List<Seat> seats = new ArrayList<>();

        for (SeatBulkCreateRequest.RowRequest row : rows) {
            for (int i = 1; i <= row.getSeatCount(); i++) {
                String seatNumber = row.getRowName() +"-"+ i;
                if (seatRepository.existsByVenueAndSeatNumber(venue, seatNumber)) {
                    throw new CustomException(ErrorCode.SEAT_ALREADY_EXISTS);
                }
                seats.add(new Seat(venue, seatNumber, row.getRowName(), row.getPriority()));
            }
        }

        List<Seat> savedSeats = seatRepository.saveAll(seats);

        return savedSeats.stream()
                .map(seat ->
                        new SeatResponse(
                                seat.getId(),
                                seat.getSeatNumber(),
                                seat.getRowName(),
                                seat.getPriority())
                ).toList();
    }

    public List<SeatResponse> findAllByVenue(Long venueId) {
        return seatRepository.findAllByVenue(
                        venueRepository.findById(venueId)
                                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND))
                ).stream()
                .map(seat -> new SeatResponse(
                        seat.getId(), seat.getSeatNumber(), seat.getRowName(), seat.getPriority()
                ))
                .toList();
    }
}