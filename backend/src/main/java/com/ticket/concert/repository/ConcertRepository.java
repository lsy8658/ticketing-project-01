package com.ticket.concert.repository;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConcertRepository extends JpaRepository<Concert, Long> {
    List<Concert> findAllByCreateBy(User user);
}
