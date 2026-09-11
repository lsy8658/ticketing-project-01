package com.ticket.concert.Controller;


import com.ticket.concert.dto.VenueCreateRequest;
import com.ticket.concert.dto.VenueResponse;
import com.ticket.concert.service.VenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<List<VenueResponse>> getVenues () {
        List<VenueResponse> venues = venueService.findAll();
        return ResponseEntity.ok(venues);
    }

    @PostMapping
    public ResponseEntity<Long> create(@Valid @RequestBody VenueCreateRequest request) {
        Long venueId = venueService.create(request.getName(), request.getAddress());
        return ResponseEntity.status(HttpStatus.CREATED).body(venueId);
    }
}