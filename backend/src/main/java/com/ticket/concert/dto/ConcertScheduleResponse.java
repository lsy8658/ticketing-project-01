package com.ticket.concert.dto;

import com.ticket.concert.domain.ConcertSchedule;
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

    public static ConcertScheduleResponse from(ConcertSchedule schedule) {
        return new ConcertScheduleResponse(
                schedule.getId(),
                new ConcertInfo(
                        schedule.getConcert().getId(),
                        schedule.getConcert().getTitle()),
                new VenueInfo(
                        schedule.getVenue().getId(),
                        schedule.getVenue().getName(),
                        schedule.getVenue().getAddress()
                ),
                schedule.getStartAt()
        );
    }

    @Getter
    public static class ConcertInfo {
        private Long id;
        private String title;

        public ConcertInfo(Long id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @Getter
    public static class VenueInfo {
        private Long id;
        private String name;
        private String address;

        public VenueInfo (Long id, String name, String address) {
            this.id = id;
            this.name = name;
            this.address = address;
        }
    }
}
