package com.ticket.concert.dto;

import com.ticket.concert.domain.Venue;
import lombok.Getter;

@Getter
public class VenueResponse {
    private Long id;
    private String name;
    private String address;

    public VenueResponse(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }

    public static VenueResponse from(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress());
    }
}
