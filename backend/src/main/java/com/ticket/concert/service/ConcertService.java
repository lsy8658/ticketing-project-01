package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.ConcertImage;
import com.ticket.concert.domain.User;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.dto.ImageInfo;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertImageRepository concertImageRepository;
    private final ImageUploadService imageUploadService;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final UserRepository userRepository;

    private List<ImageInfo> getImages(Concert concert) {
        return concertImageRepository
                .findAllByConcertOrderBySortOrderAsc(concert)
                .stream()
                .map(image -> new ImageInfo(image.getImageUrl(), image.getPublicId()))
                .toList();
    }

    public Long create(
            Long userId,
            String title,
            String description,
            String imageUrl,
            List<ImageInfo> images
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Concert concert = Concert.builder()
                .title(title)
                .description(description)
                .imageUrl(imageUrl)
                .createBy(user)
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
    @Transactional
    public ConcertResponse update(Long id, Long userId, ConcertUpdateRequest request) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        if (!concert.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

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

    @Transactional
    public void delete(Long id, Long userId) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        if (!concert.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        if (concertScheduleRepository.existsByConcert(concert)) {
            throw new CustomException(ErrorCode.CONCERT_HAS_SCHEDULE);
        }

        List<ConcertImage> images = concertImageRepository.findAllByConcertOrderBySortOrderAsc(concert);
        for (ConcertImage image : images) {
            imageUploadService.delete(image.getPublicId());
        }
        concertImageRepository.deleteAllByConcert(concert);
        seatGradeRepository.deleteAllByConcert(concert);

        concertRepository.deleteById(id);
    }
}