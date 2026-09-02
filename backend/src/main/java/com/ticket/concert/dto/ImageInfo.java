package com.ticket.concert.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ImageInfo {
    private String url;
    private String publicId;

    public ImageInfo(String url, String publicId) {
        this.url = url;
        this.publicId = publicId;
    }
}