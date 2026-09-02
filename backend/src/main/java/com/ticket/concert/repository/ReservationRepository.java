package com.ticket.concert.repository;

import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(User user);
}
