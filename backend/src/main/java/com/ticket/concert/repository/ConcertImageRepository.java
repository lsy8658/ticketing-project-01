package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConcertImageRepository extends JpaRepository<ConcertImage, Long> {

    List<ConcertImage> findAllByConcertOrderBySortOrderAsc(Concert concert);

    @Query("SELECT ci FROM ConcertImage ci WHERE ci.concert IN :concerts ORDER BY ci.concert.id, ci.sortOrder")
    List<ConcertImage> findAllByConcertIn(@Param("concerts") List<Concert> concerts);
    void deleteAllByConcert(Concert concert);
}