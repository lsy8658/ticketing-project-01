package com.ticket.concert.Controller;


import com.ticket.concert.dto.VenueCreateRequest;
import com.ticket.concert.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/venues")
public class VenueController {
    private final VenueService venueService;

    @PostMapping
    public ResponseEntity<Long> create (@RequestBody VenueCreateRequest request) {
        Long venueId = venueService.create(request.getName(), request.getAddress());
        return ResponseEntity.ok(venueId);
    }
}
