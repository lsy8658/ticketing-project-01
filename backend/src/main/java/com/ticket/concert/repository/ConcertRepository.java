package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertStatus;
import com.ticket.concert.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    List<Concert> findAllByCreateBy(User user);
    Page<Concert> findAllByStatusNot(ConcertStatus status, Pageable pageable);
}