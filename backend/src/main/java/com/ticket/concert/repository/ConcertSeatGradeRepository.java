package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertSeatGrade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConcertSeatGradeRepository extends JpaRepository<ConcertSeatGrade, Long> {
    List<ConcertSeatGrade> findAllByConcert(Concert concert);
    Optional<ConcertSeatGrade> findByConcertAndRowName(Concert concert, String rowName);
    void deleteAllByConcert(Concert concert);
}