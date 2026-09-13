package com.ticket.concert.Controller;

import com.ticket.concert.dto.ConcertCreateRequest;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.service.ConcertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @PostMapping
    public ResponseEntity<Long> createConcert(
            Authentication authentication,
            @Valid @RequestBody ConcertCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long id = concertService.create(
                userId, request.getTitle(), request.getDescription(), request.getImageUrl(), request.getImages()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConcertResponse> getConcert (@PathVariable("id") Long id) {
        ConcertResponse concertResponse = concertService.findConcert(id);
        return ResponseEntity.ok(concertResponse);
    }

    @GetMapping
    public ResponseEntity<List<ConcertResponse>> getConcerts () {
        List<ConcertResponse> concerts = concertService.findAll();
        return ResponseEntity.ok(concerts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConcertResponse> updateConcert (
            Authentication authentication,
            @PathVariable("id") Long id,
            @RequestBody ConcertUpdateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        ConcertResponse concert =  concertService.update(id, userId, request);
        return ResponseEntity.ok(concert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert (
            Authentication authentication,
            @PathVariable("id") Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        concertService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
