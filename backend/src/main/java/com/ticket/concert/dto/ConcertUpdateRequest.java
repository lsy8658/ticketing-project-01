package com.ticket.concert.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConcertUpdateRequest {
    private String title;
    private String description;
    private String imageUrl;
}
