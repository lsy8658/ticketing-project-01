package com.ticket.concert.service;

import com.ticket.concert.domain.User;
import com.ticket.concert.domain.Venue;
import com.ticket.concert.dto.VenueResponse;
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

    public List<VenueResponse> findMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return venueRepository.findAllByCreateBy(user).stream()
                .map(VenueResponse::from)
                .toList();
    }


}
