package com.ticket.concert.domain;

import jakarta.persistence.*;
        import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ConcertSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private LocalDateTime startAt;

    public ConcertSchedule(Concert concert, Venue venue, LocalDateTime startAt) {
        this.concert = concert;
        this.venue = venue;
        this.startAt = startAt;
    }
}
