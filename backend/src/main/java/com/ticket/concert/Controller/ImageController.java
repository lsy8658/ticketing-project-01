package com.ticket.concert.Controller;

import com.ticket.concert.dto.ImageInfo;
import com.ticket.concert.service.ImageUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<List<ImageInfo>> upload(@RequestParam("files") List<MultipartFile> files) {
        List<ImageInfo> result = imageUploadService.uploadImages(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
