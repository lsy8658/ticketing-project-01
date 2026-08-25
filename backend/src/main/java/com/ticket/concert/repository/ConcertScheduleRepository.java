package com.ticket.concert.repository;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
    boolean existsByVenueAndStartAt(Venue venue, LocalDateTime startAt);
}
