package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConcertSeatGrade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(nullable = false)
    private String rowName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_grade_id", nullable = false)
    private SeatGrade seatGrade;

    public ConcertSeatGrade(Concert concert, String rowName, SeatGrade seatGrade) {
        this.concert = concert;
        this.rowName = rowName;
        this.seatGrade = seatGrade;
    }

    public void updateSeatGrade(SeatGrade seatGrade) {
        this.seatGrade = seatGrade;
    }
}
