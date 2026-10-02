package com.ticket.concert.dto;

import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.ReservationStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ReservationResponse {
    private Long id;
    private Long concertScheduleId;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
    private String concertTitle;
    private String venueName;
    private LocalDateTime scheduleStartAt;
    private List<ReservationDetailResponse.SeatInfo> seats;
    private Long totalAmount;
    private boolean paid;

    public ReservationResponse(
            Long id,
            Long concertScheduleId,
            ReservationStatus status,
            LocalDateTime reservedAt,
            String concertTitle,
            String venueName,
            LocalDateTime scheduleStartAt,
            List<ReservationDetailResponse.SeatInfo> seats,
            Long totalAmount,
            boolean paid
    ) {
        this.id = id;
        this.concertScheduleId = concertScheduleId;
        this.status = status;
        this.reservedAt = reservedAt;
        this.concertTitle = concertTitle;
        this.venueName = venueName;
        this.scheduleStartAt = scheduleStartAt;
        this.seats = seats;
        this.totalAmount = totalAmount;
        this.paid = paid;
    }

    public static ReservationResponse from(
            Reservation reservation,
            List<ReservationDetailResponse.SeatInfo> seats,
            boolean paid
    ) {
        long total = seats.stream()
                .mapToLong(ReservationDetailResponse.SeatInfo::getPrice)
                .sum();
        return new ReservationResponse(
                reservation.getId(),
                reservation.getConcertSchedule().getId(),
                reservation.getStatus(),
                reservation.getReservedAt(),
                reservation.getConcertSchedule().getConcert().getTitle(),
                reservation.getConcertSchedule().getVenue().getName(),
                reservation.getConcertSchedule().getStartAt(),
                seats,
                total,
                paid
        );
    }
}