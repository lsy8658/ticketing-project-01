package com.ticket.concert.repository;

import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.ReservationSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findAllByReservation(Reservation reservation);
}
