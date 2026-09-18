package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Venue;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    boolean existsByVenueAndStartAt(Venue venue, LocalDateTime startAt);
    List<ConcertSchedule> findAllByConcertId(Long concertId);
    boolean existsByConcert(Concert concert);
    Optional<ConcertSchedule> findFirstByConcertId(Long concertId);
    @Query("SELECT COUNT(cs) > 0 FROM ConcertSchedule cs WHERE cs.venue = :venue AND FUNCTION('DATE', cs.startAt) <= :endDate AND FUNCTION('DATE', cs.endAt) >= :startDate")
    boolean existsOverlappingSchedule(@Param("venue") Venue venue, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(cs) > 0 FROM ConcertSchedule cs WHERE cs.venue = :venue AND cs.id <> :scheduleId AND FUNCTION('DATE', cs.startAt) <= :endDate AND FUNCTION('DATE', cs.endAt) >= :startDate")
    boolean existsOverlappingScheduleExcludingSelf(
            @Param("venue") Venue venue,
            @Param("scheduleId") Long scheduleId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    boolean existsByVenue(Venue venue);
}
