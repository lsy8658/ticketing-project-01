package com.ticket.concert.repository;

import com.ticket.concert.domain.ConcertSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertScheduleRepository extends JpaRepository<ConcertSchedule, Long> {
}
