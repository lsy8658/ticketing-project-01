package com.ticket.concert.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReservationDetailResponse {

    private Long reservationId;
    private List<SeatInfo> seats;
    private Long totalAmount;

    public ReservationDetailResponse(Long reservationId, List<SeatInfo> seats, Long totalAmount) {
        this.reservationId = reservationId;
        this.seats = seats;
        this.totalAmount = totalAmount;
    }

    @Getter
    public static class SeatInfo {
        private String seatNumber;
        private String seatGradeName;
        private Long price;

        public SeatInfo(String seatNumber, String seatGradeName, Long price) {
            this.seatNumber = seatNumber;
            this.seatGradeName = seatGradeName;
            this.price = price;
        }
    }
}