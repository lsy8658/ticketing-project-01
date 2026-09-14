package com.ticket.concert.repository;

import com.ticket.concert.domain.Payment;
import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByReservation(Reservation reservation);
    List<Payment> findAllByReservation_User(User user);
    Optional<Payment> findByReservation(Reservation reservation);
}