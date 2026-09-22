package com.ticket.concert.dto;

import com.ticket.concert.domain.Venue;
import lombok.Getter;

@Getter
public class VenueResponse {
    private Long id;
    private String name;
    private String address;
    private int capacity;
    private String managerPhone;

    public VenueResponse(Long id, String name, String address, int capacity, String managerPhone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.managerPhone = managerPhone;
    }

    public static VenueResponse from(Venue venue) {
        return new VenueResponse(venue.getId(), venue.getName(), venue.getAddress(),
                venue.getCapacity(), venue.getManagerPhone());
    }
}
