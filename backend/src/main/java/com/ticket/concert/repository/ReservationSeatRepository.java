package com.ticket.concert.repository;

import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.ReservationSeat;
import com.ticket.concert.domain.ReservationStatus;
import com.ticket.concert.domain.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationSeatRepository extends JpaRepository<ReservationSeat, Long> {
    List<ReservationSeat> findAllByReservation(Reservation reservation);
    Optional<ReservationSeat> findByScheduleSeat(ScheduleSeat scheduleSeat);
    void deleteAllByReservationIn(List<Reservation> reservations);
    Optional<ReservationSeat> findByScheduleSeatAndReservation_Status(
            ScheduleSeat scheduleSeat,
            ReservationStatus status
    );
    @Query("SELECT COUNT(rs) FROM ReservationSeat rs " +
            "WHERE rs.reservation.user.id = :userId " +
            "AND rs.reservation.concertSchedule.id = :concertScheduleId " +
            "AND rs.reservation.status = :status")
    long countByUserAndSchedule(
            @Param("userId") Long userId,
            @Param("concertScheduleId") Long concertScheduleId,
            @Param("status") ReservationStatus status
    );
}
