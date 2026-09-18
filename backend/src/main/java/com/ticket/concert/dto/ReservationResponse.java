package com.ticket.concert.dto;

import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReservationResponse {
    private Long id;
    private Long concertScheduleId;
    private ReservationStatus status;
    private LocalDateTime reservedAt;

    public ReservationResponse(Long id, Long concertScheduleId,
                               ReservationStatus status, LocalDateTime reservedAt) {
        this.id = id;
        this.concertScheduleId = concertScheduleId;
        this.status = status;
        this.reservedAt = reservedAt;
    }

    public static ReservationResponse from(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getConcertSchedule().getId(),
                reservation.getStatus(),
                reservation.getReservedAt()
        );
    }
}