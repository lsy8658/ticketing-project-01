package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"venue_id", "seat_number"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private int priority;

    @Column(nullable = false)
    private String rowName;

    public Seat(Venue venue, String seatNumber, String rowName, int priority) {
        this.venue = venue;
        this.seatNumber = seatNumber;
        this.rowName = rowName;
        this.priority = priority;
    }
}