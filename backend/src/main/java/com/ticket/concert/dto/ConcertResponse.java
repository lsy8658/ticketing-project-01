package com.ticket.concert.dto;

import lombok.Getter;

import java.util.List;

@Getter

public class ConcertResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;

    public ConcertResponse(Long id, String title, String description, String imageUrl, List<String> imageUrls) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageUrls = imageUrls;

    }
}
