package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertImage;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.ConcertImageRepository;
import com.ticket.concert.repository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertImageRepository concertImageRepository;

    private List<String> getImageUrls (Concert concert) {
        List<String> imageUrls = concertImageRepository
                .findAllByConcertOrderBySortOrderAsc(concert)
                .stream().map(ConcertImage::getImageUrl).toList();
        return imageUrls;
    }
    public Long create (String title, String description, String imageUrl, List<String> imageUrls) {
        Concert concert = Concert.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .build();
        Concert savedConcert = concertRepository.save(concert);

        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                ConcertImage image = new ConcertImage(savedConcert, imageUrls.get(i), i);
                concertImageRepository.save(image);
            }
        }

        return savedConcert.getId();
    }

    public ConcertResponse findConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException((ErrorCode.CONCERT_NOT_FOUND)));

        List<String> imageUrls = getImageUrls(concert);
        return new ConcertResponse(concert.getId(), concert.getTitle(),
                concert.getDescription(), concert.getImageUrl(), imageUrls);
    }

    public List<ConcertResponse> findAll() {
        return concertRepository.findAll().stream().map(concert -> {

            List<String> imageUrls = getImageUrls(concert);
            return new ConcertResponse(concert.getId(), concert.getTitle(),
                    concert.getDescription(), concert.getImageUrl(), imageUrls);
        }).toList();
    }

    public ConcertResponse update(Long id, ConcertUpdateRequest request) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        concert.update(request.getTitle(), request.getDescription(), request.getImageUrl());

        concertImageRepository.deleteAllByConcert(concert);
        List<String> newUrls = request.getImageUrls();
        if (newUrls != null) {
            for (int i = 0; i < newUrls.size(); i++) {
                concertImageRepository.save(new ConcertImage(concert, newUrls.get(i), i));
            }
        }

        return new ConcertResponse(id, request.getTitle(), request.getDescription(),
                request.getImageUrl(), newUrls);
    }

    public void delete(Long id) {
        concertRepository.deleteById(id);
    }
}
