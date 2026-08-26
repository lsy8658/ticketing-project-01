package com.ticket.concert.repository;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    boolean existsByVenueAndStartAt(Venue venue, LocalDateTime startAt);
    List<ConcertSchedule> findAllByConcertId(Long concertId);
}
