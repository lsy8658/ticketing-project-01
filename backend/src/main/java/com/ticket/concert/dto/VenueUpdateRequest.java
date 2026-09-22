package com.ticket.concert.dto;

import lombok.Getter;

@Getter
public class VenueUpdateRequest {
    private String name;
    private String address;
    private int capacity;
    private String managerPhone;
}
