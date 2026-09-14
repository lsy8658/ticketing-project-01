package com.ticket.concert.dto;

import com.ticket.concert.domain.ConcertStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ConcertResponse {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private ConcertStatus status;
    private LocalDateTime salesStartAt;
    private LocalDateTime salesEndAt;
    private List<ImageInfo> images;

    public ConcertResponse(Long id, String title, String description, String imageUrl,
                           ConcertStatus status, LocalDateTime salesStartAt, LocalDateTime salesEndAt,
                           List<ImageInfo> images) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.salesStartAt = salesStartAt;
        this.salesEndAt = salesEndAt;
        this.images = images;
    }
}