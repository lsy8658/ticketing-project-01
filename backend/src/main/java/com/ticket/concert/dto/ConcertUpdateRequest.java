package com.ticket.concert.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ConcertUpdateRequest {
    private String title;
    private String description;
    private String imageUrl;
    private List<String> imageUrls;
}
