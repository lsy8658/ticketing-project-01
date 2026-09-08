package com.ticket.concert.dto;

import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.domain.SeatStatus;
import lombok.Getter;

@Getter
public class ScheduleSeatResponse {

    private Long scheduleSeatId;
    private String seatNumber;
    private SeatStatus status;
    private String seatGradeName;
    private Long price;

    public ScheduleSeatResponse(Long scheduleSeatId, String seatNumber, SeatStatus status, String seatGradeName, Long price) {
        this.scheduleSeatId = scheduleSeatId;
        this.seatNumber = seatNumber;
        this.status = status;
        this.seatGradeName = seatGradeName;
        this.price = price;
    }

    public static ScheduleSeatResponse from(ScheduleSeat scheduleSeat) {
        return new ScheduleSeatResponse(
                scheduleSeat.getId(),
                scheduleSeat.getSeat().getSeatNumber(),
                scheduleSeat.getStatus(),
                scheduleSeat.getSeat().getSeatGrade().getName(),
                scheduleSeat.getSeat().getSeatGrade().getPrice()
        );
    }
}