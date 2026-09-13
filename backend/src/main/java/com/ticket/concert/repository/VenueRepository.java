package com.ticket.concert.repository;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findAllByCreateBy(User user);
}
