package com.ticket.concert.repository;

import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.domain.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleSeatRepository  extends JpaRepository<ScheduleSeat, Long> {
    List<ScheduleSeat> findAllByStatus(SeatStatus status);
}
