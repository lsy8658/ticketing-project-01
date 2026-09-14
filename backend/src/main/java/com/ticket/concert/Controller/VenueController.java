package com.ticket.concert.Controller;


import com.ticket.concert.dto.VenueCreateRequest;
import com.ticket.concert.dto.VenueResponse;
import com.ticket.concert.dto.VenueUpdateRequest;
import com.ticket.concert.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;



    @PostMapping
    public ResponseEntity<Long> create(
            Authentication authentication,
            @Valid @RequestBody VenueCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        Long venueId = venueService.create(userId, request.getName(), request.getAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(venueId);
    }

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getAll () {
        return ResponseEntity.ok(venueService.findAll());
    }

    @GetMapping("/mine")
    public ResponseEntity<List<VenueResponse>> getMine(
            Authentication authentication
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(venueService.findMine(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VenueResponse> upadte(
            Authentication authentication,
            @PathVariable("id") Long id,
            @RequestBody VenueUpdateRequest request
            ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(venueService.update(id, userId, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            Authentication authentication,
            @PathVariable("id") Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        venueService.delete(id,userId);
        return ResponseEntity.noContent().build();
    }
}