package com.ticket.concert.dto;

import lombok.Getter;

@Getter

public class ConcertResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;

    public ConcertResponse(Long id, String title, String description, String imageUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
