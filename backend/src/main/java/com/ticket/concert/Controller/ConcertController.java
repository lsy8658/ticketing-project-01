package com.ticket.concert.Controller;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.dto.ConcertCreateRequest;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.service.ConcertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concerts")
public class ConcertController {
    private final ConcertService concertService;

    @PostMapping
    public ResponseEntity<Long> createConcert (@Valid @RequestBody ConcertCreateRequest request) {
        Long id = concertService.create(request.getTitle(), request.getDescription(), request.getImageUrl());
        return ResponseEntity.ok(id);
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
    public ResponseEntity<ConcertResponse> updateConcert (@PathVariable("id") Long id, @RequestBody ConcertUpdateRequest request) {
        ConcertResponse concert =  concertService.update(id, request);
        return ResponseEntity.ok(concert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConcert (@PathVariable("id") Long id) {
        concertService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
