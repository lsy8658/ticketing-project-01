package com.ticket.concert.dto;

import lombok.Getter;

import java.util.List;

@Getter

public class ConcertResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private List<ImageInfo> images;

    public ConcertResponse(Long id, String title, String description, String imageUrl, List<ImageInfo> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.images = images;

    }
}
