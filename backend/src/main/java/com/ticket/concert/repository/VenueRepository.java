package com.ticket.concert.repository;

import com.ticket.concert.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Long> {
}
