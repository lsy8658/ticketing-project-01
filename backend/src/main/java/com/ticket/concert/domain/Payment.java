package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Long amount;

    public Payment(Reservation reservation, Long amount) {
        this.reservation = reservation;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }

    public void complete() {
        this.status = PaymentStatus.PAID;
    }

    public void fail() {
        this.status = PaymentStatus.FAILED;
    }
}