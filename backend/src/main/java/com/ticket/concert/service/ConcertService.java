package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertImage;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.dto.ImageInfo;
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
    private final ImageUploadService imageUploadService;

    private List<ImageInfo> getImages(Concert concert) {
        return concertImageRepository
                .findAllByConcertOrderBySortOrderAsc(concert)
                .stream()
                .map(image -> new ImageInfo(image.getImageUrl(), image.getPublicId()))
                .toList();
    }

    public Long create(String title, String description, String imageUrl, List<ImageInfo> images) {
        Concert concert = Concert.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .build();
        Concert savedConcert = concertRepository.save(concert);

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                ImageInfo info = images.get(i);
                ConcertImage image = new ConcertImage(savedConcert, info.getUrl(), info.getPublicId(), i);
                concertImageRepository.save(image);
            }
        }

        return savedConcert.getId();
    }

    public ConcertResponse findConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        List<ImageInfo> images = getImages(concert);
        return new ConcertResponse(concert.getId(), concert.getTitle(),
                concert.getDescription(), concert.getImageUrl(), images);
    }

    public List<ConcertResponse> findAll() {
        return concertRepository.findAll().stream().map(concert -> {
            List<ImageInfo> images = getImages(concert);
            return new ConcertResponse(concert.getId(), concert.getTitle(),
                    concert.getDescription(), concert.getImageUrl(), images);
        }).toList();
    }

    public ConcertResponse update(Long id, ConcertUpdateRequest request) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        concert.update(request.getTitle(), request.getDescription(), request.getImageUrl());

        List<ConcertImage> oldImages = concertImageRepository.findAllByConcertOrderBySortOrderAsc(concert);
        List<ImageInfo> newImages = request.getImages();

        List<String> newPublicIds = newImages == null
                ? List.of()
                : newImages.stream().map(ImageInfo::getPublicId).toList();

        for (ConcertImage oldImage : oldImages) {
            if (!newPublicIds.contains(oldImage.getPublicId())) {
                imageUploadService.delete(oldImage.getPublicId());
            }
        }
        concertImageRepository.deleteAllByConcert(concert);
        if (newImages != null) {
            for (int i = 0; i < newImages.size(); i++) {
                ImageInfo info = newImages.get(i);
                concertImageRepository.save(new ConcertImage(concert, info.getUrl(), info.getPublicId(), i));
            }
        }

        return new ConcertResponse(id, request.getTitle(), request.getDescription(),
                request.getImageUrl(), newImages);
    }

    public void delete(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        List<ConcertImage> images = concertImageRepository.findAllByConcertOrderBySortOrderAsc(concert);
        for (ConcertImage image : images) {
            imageUploadService.delete(image.getPublicId());
        }
        concertImageRepository.deleteAllByConcert(concert);

        concertRepository.deleteById(id);
    }
}