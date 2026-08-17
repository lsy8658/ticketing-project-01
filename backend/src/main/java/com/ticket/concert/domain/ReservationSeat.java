package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_seat_id", nullable = false)
    private ScheduleSeat scheduleSeat;

    public ReservationSeat(Reservation reservation, ScheduleSeat scheduleSeat) {
        this.reservation = reservation;
        this.scheduleSeat = scheduleSeat;
    }
}