package com.ticket.concert.repository;

import com.ticket.concert.domain.Payment;
import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByReservation(Reservation reservation);
    List<Payment> findAllByReservation_User(User user);
}
