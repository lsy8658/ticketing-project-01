package com.ticket.concert.repository;

import com.ticket.concert.domain.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleSeatRepository  extends JpaRepository<ScheduleSeat, Long> {
}
