package com.ticket.concert.service;

import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.VenueResponse;
import com.ticket.concert.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;

    public Long create (String name, String address) {
        Venue venue = new Venue(name, address);
        return venueRepository.save(venue).getId();
    }

    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream()
                .map(VenueResponse::from)
                .toList();
    }
}
