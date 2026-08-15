package com.ticket.concert.dto;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConcertScheduleResponse {

    private Long id;
    private ConcertInfo concert;
    private VenueInfo venue;
    private LocalDateTime startAt;

    public ConcertScheduleResponse (
        Long id,
        ConcertInfo concert,
        VenueInfo venue,
        LocalDateTime startAt
    ) {
        this.id = id;
        this.concert = concert;
        this.venue = venue;
        this.startAt = startAt;
    }

    @Getter
    public static class ConcertInfo {

    }

    @Getter
    public static class VenueInfo {

    }
}
