package com.ticket.concert.repository;

import com.ticket.concert.domain.Seat;
import com.ticket.concert.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByVenue(Venue venue);
    Boolean existsByVenueAndSeatNumber(Venue venue, String seatNumber);
}
