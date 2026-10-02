package com.ticket.concert.repository;

import com.ticket.concert.domain.Payment;
import com.ticket.concert.domain.PaymentStatus;
import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    boolean existsByReservation(Reservation reservation);
    List<Payment> findAllByReservation_User(User user);
    Optional<Payment> findByReservation(Reservation reservation);
    @Query(
            "SELECT payment.reservation.id FROM Payment payment "
            + "WHERE payment.reservation.user = :user "
            + "AND payment.status = :status")
    List<Long> findReservationIdsByUserAndStatus(
            @Param("user") User user,
            @Param("status") PaymentStatus status
    );
}