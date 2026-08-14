package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;

    public Long create (String title, String description, String imageUrl) {
        Concert concert = Concert.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .build();
        return concertRepository.save(concert).getId();
    }

    public ConcertResponse findConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(("해당 콘서트가 없습니다.")));
        return new ConcertResponse(concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getImageUrl());
    }

    public List<ConcertResponse> findAll() {
        return concertRepository.findAll().stream().map(concert -> new ConcertResponse(
                concert.getId(),
                concert.getTitle(),
                concert.getDescription(),
                concert.getImageUrl())).toList();
    }

    public ConcertResponse update(Long id, ConcertUpdateRequest request) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 콘서트가 없습니다."));
        concert.update(request.getTitle(), request.getDescription(), request.getImageUrl());
        return new ConcertResponse(id, request.getTitle(), request.getDescription(), request.getImageUrl());
    }

    public void delete(Long id) {
        concertRepository.deleteById(id);
    }
}
