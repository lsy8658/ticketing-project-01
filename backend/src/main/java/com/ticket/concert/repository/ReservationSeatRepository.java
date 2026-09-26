package com.ticket.concert.repository;

import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.ReservationSeat;
import com.ticket.concert.domain.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findAllByReservation(Reservation reservation);
    Optional<ReservationSeat> findByScheduleSeat(ScheduleSeat scheduleSeat);
    void deleteAllByReservationIn(List<Reservation> reservations);
}
