package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_schedule_id", nullable = false)
    private ConcertSchedule concertSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    public ScheduleSeat(ConcertSchedule concertSchedule, Seat seat) {
        this.concertSchedule = concertSchedule;
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
    }

    public void hold() {
        this.status = SeatStatus.HOLDING;
    }

    public void reserve() {
        this.status = SeatStatus.RESERVED;
    }

    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }

}