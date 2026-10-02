package com.ticket.concert.repository;

import com.ticket.concert.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);
    @Query("SELECT reservation FROM Reservation reservation "
            + "JOIN FETCH reservation.concertSchedule schedule "
            + "JOIN FETCH schedule.concert "
            + "JOIN FETCH schedule.venue "
            + "WHERE reservation.user = :user "
            + "ORDER BY reservation.reservedAt DESC")
    List<Reservation> findAllWithScheduleByUser(@Param("user") User user);
    boolean existsByConcertScheduleAndStatus(ConcertSchedule concertSchedule, ReservationStatus status);
    List<Reservation> findAllByConcertSchedule_Concert(Concert concert);
}
