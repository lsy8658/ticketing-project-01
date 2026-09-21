package com.ticket.concert.repository;

import com.ticket.concert.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);
    boolean existsByConcertScheduleAndStatus(ConcertSchedule concertSchedule, ReservationStatus status);
    List<Reservation> findAllByConcertSchedule_Concert(Concert concert);
}
