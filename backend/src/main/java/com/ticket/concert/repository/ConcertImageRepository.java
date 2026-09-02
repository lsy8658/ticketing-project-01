package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertImageRepository extends JpaRepository<ConcertImage, Long> {
    List<ConcertImage> findAllByConcertOrderBySortOrderAsc(Concert concert);
    void deleteAllByConcert(Concert concert);
}