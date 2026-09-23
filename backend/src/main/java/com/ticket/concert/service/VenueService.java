package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.VenueResponse;
import com.ticket.concert.dto.VenueUpdateRequest;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.ConcertScheduleRepository;
import com.ticket.concert.repository.SeatRepository;
import com.ticket.concert.repository.UserRepository;
import com.ticket.concert.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatRepository seatRepository;

    public Long create (Long userId, String name, String address, int capacity, String managerPhone) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (venueRepository.existsByNameAndAddress(name, address)) {
            throw new CustomException(ErrorCode.VENUE_ALREADY_EXISTS);
        }

        Venue venue = new Venue(name, address, capacity, managerPhone, user);
        return venueRepository.save(venue).getId();
    }

    public List<VenueResponse> findAll() {
        return venueRepository.findAll().stream()
                .map(venue -> VenueResponse.from(venue, seatRepository.existsByVenue(venue)))
                .toList();
    }

    public List<VenueResponse> findMine(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return venueRepository.findAllByCreateBy(user).stream()
                .map(venue -> VenueResponse.from(venue, seatRepository.existsByVenue(venue)))
                .toList();
    }


    public VenueResponse findById(Long venueId) {
        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
        return VenueResponse.from(venue, seatRepository.existsByVenue(venue));
    }

    public Venue findOwned(Long venueId, Long userId) {
        return venueRepository.findById(venueId)
                .orElseThrow(() -> new CustomException(ErrorCode.VENUE_NOT_FOUND));
    }

    @Transactional
    public VenueResponse update(Long venueId, Long userId, VenueUpdateRequest request) {
        Venue venue = findOwned(venueId, userId);
        venue.update(request.getName(), request.getAddress(), request.getCapacity(), request.getManagerPhone());
        return VenueResponse.from(
                venue,
                seatRepository.existsByVenue(venue)
        );
    }

    @Transactional
    public void delete(Long venueId, Long userId) {
        Venue venue = findOwned(venueId, userId);

        if (concertScheduleRepository.existsByVenue(venue)) {
            throw new CustomException(ErrorCode.VENUE_HAS_SCHEDULE);
        }

        if (seatRepository.existsByVenue(venue)) {
            throw new CustomException(ErrorCode.VENUE_HAS_SEATS);
        }
        venueRepository.delete(venue);
    }
}
