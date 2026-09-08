package com.ticket.concert.repository;

import com.ticket.concert.domain.SeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatGradeRepository extends JpaRepository<SeatGrade, Long> {
    List<SeatGrade> findAllByConcertId(Long concertId);
}