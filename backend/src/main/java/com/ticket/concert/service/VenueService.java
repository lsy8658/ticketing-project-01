package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.VenueResponse;
import com.ticket.concert.dto.VenueUpdateRequest;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.UserRepository;
import com.ticket.concert.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;

    public Long create (Long userId, String name, String address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Venue venue = new Venue(name, address, user);
        return venueRepository.save(venue).getId();
    }

    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream()
                .map(VenueResponse::from)
                .toList();
    }

    public List<VenueResponse> findMine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return venueRepository.findAllByCreateBy(user).stream()
                .map(VenueResponse::from)
                .toList();
    }

    public Venue findOwned(Long venueId, Long userId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        if (!venue.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        return venue;
    }

    public VenueResponse update(Long venueId, Long userId, VenueUpdateRequest request) {
        Venue venue = findOwned(venueId, userId);
        venue.update(request.getName(), request.getAddress());
        return VenueResponse.from(venue);
    }

    public void delete(Long venueId, Long userId) {
        Venue venue = findOwned(venueId, userId);
        venueRepository.delete(venue);
    }
}
