package com.ticket.concert.repository;

import com.ticket.concert.domain.Payment;
import com.ticket.concert.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByReservation(Reservation reservation);
}
