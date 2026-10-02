package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Venue;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    boolean existsByVenueAndStartAt(Venue venue, LocalDateTime startAt);

    List<ConcertSchedule> findAllByConcertId(Long concertId);

    boolean existsByConcert(Concert concert);

    Optional<ConcertSchedule> findFirstByConcertId(Long concertId);

    @Query("""
           SELECT COUNT(cs) > 0
           FROM ConcertSchedule cs
           WHERE cs.venue = :venue
           AND cs.startAt < :endAt
           AND cs.endAt > :startAt
           """)
    boolean existsOverlappingSchedule(
            @Param("venue") Venue venue,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    @Query("""
           SELECT COUNT(cs) > 0
           FROM ConcertSchedule cs
           WHERE cs.venue = :venue
           AND cs.id <> :scheduleId
           AND cs.startAt < :endAt
           AND cs.endAt > :startAt
           """)
    boolean existsOverlappingScheduleExcludingSelf(
            @Param("venue") Venue venue,
            @Param("scheduleId") Long scheduleId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    boolean existsByVenue(Venue venue);
}