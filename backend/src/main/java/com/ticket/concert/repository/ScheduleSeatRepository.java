package com.ticket.concert.repository;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {
    List<ScheduleSeat> findAllByStatus(SeatStatus status);
    boolean existsByConcertSchedule(ConcertSchedule concertSchedule);
    void deleteAllByConcertSchedule(ConcertSchedule concertSchedule);
    List<ScheduleSeat> findAllByConcertScheduleId(Long concertScheduleId);
    boolean existsByConcertScheduleAndSeatIdIn(ConcertSchedule concertSchedule, List<Long> seatIds);

    @Query("SELECT scheduleSeat FROM ScheduleSeat scheduleSeat " +
            "JOIN FETCH scheduleSeat.seat " +
            "JOIN FETCH scheduleSeat.seatGrade " +
            "WHERE scheduleSeat.concertSchedule.id = :concertScheduleId")
    List<ScheduleSeat> findAllWithSeatAndGradeByConcertScheduleId(@Param("concertScheduleId") Long concertScheduleId);
    List<ScheduleSeat> findAllByStatusAndHoldAtBefore(
            SeatStatus status,
            LocalDateTime time
    );
}