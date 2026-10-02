package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.dto.ImageInfo;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConcertService {
    private final ConcertRepository concertRepository;
    private final ConcertImageRepository concertImageRepository;
    private final ImageUploadService imageUploadService;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final SeatGradeRepository seatGradeRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final ConcertSeatGradeRepository concertSeatGradeRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final TransactionTemplate transactionTemplate;

    private List<ImageInfo> getImages(Concert concert) {
        return concertImageRepository
                .findAllByConcertOrderBySortOrderAsc(concert)
                .stream()
                .map(image -> new ImageInfo(image.getImageUrl(), image.getPublicId()))
                .toList();
    }

    private Map<Long, List<ImageInfo>> getImagesGroupedByConcert(List<Concert> concerts) {
        List<ConcertImage> allImages = concertImageRepository.findAllByConcertIn(concerts);
        return allImages.stream()
                .collect(Collectors.groupingBy(
                        image -> image.getConcert().getId(),
                        Collectors.mapping(
                                image -> new ImageInfo(image.getImageUrl(), image.getPublicId()),
                                Collectors.toList()
                        )
                ));
    }

    @Transactional
    public Long create(
            Long userId,
            String title,
            String description,
            String imageUrl,
            LocalDateTime salesStartAt,
            LocalDateTime salesEndAt,
            List<ImageInfo> images
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!salesStartAt.isBefore(salesEndAt)) {
            throw new CustomException(ErrorCode.CONCERT_SALES_PERIOD_INVALID);
        }

        Concert concert = Concert.builder()
                .title(title).description(description).imageUrl(imageUrl)
                .createBy(user).salesStartAt(salesStartAt).salesEndAt(salesEndAt)
                .build();
        Concert savedConcert = concertRepository.save(concert);

        if (images != null) {
            for (int i = 0; i < images.size(); i++) {
                ImageInfo info = images.get(i);
                concertImageRepository.save(new ConcertImage(savedConcert, info.getUrl(), info.getPublicId(), i));
            }
        }
        return savedConcert.getId();
    }

    public List<ConcertResponse> findMyConcerts(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Concert> concerts = concertRepository.findAllByCreateBy(user);
        Map<Long, List<ImageInfo>> imagesByConcert = getImagesGroupedByConcert(concerts);

        return concerts.stream()
                .map(concert -> new ConcertResponse(
                        concert.getId(), concert.getTitle(), concert.getDescription(), concert.getImageUrl(),
                        concert.getStatus(), concert.getSalesStartAt(), concert.getSalesEndAt(),
                        imagesByConcert.getOrDefault(concert.getId(), List.of())
                )).toList();
    }

    public ConcertResponse findConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
        return new ConcertResponse(concert.getId(), concert.getTitle(),
                concert.getDescription(), concert.getImageUrl(),
                concert.getStatus(), concert.getSalesStartAt(), concert.getSalesEndAt(),
                getImages(concert));
    }

    public List<ConcertResponse> findAll(int page, int size) {
        int safeSize = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(Math.max(page, 0), safeSize, Sort.by("id").descending());

        List<Concert> concerts = concertRepository
                .findAllByStatusNot(ConcertStatus.SUSPENDED, pageable)
                .getContent();

        if (concerts.isEmpty()) {
            return List.of();
        }

        Map<Long, List<ImageInfo>> imagesByConcert = getImagesGroupedByConcert(concerts);

        return concerts.stream()
                .map(c -> new ConcertResponse(c.getId(), c.getTitle(), c.getDescription(), c.getImageUrl(),
                        c.getStatus(), c.getSalesStartAt(), c.getSalesEndAt(),
                        imagesByConcert.getOrDefault(c.getId(), List.of())))
                .toList();
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
        List<String> newPublicIds = newImages == null ? List.of() : newImages.stream().map(ImageInfo::getPublicId).toList();
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
        return new ConcertResponse(id, request.getTitle(), request.getDescription(), request.getImageUrl(),
                concert.getStatus(), concert.getSalesStartAt(), concert.getSalesEndAt(), newImages);
    }

    public void delete(Long userId, Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        if (!concert.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        List<Reservation> reservations =
                reservationRepository.findAllByConcertSchedule_Concert(concert);
        boolean hasPaymentHistory = false;

        for (Reservation reservation : reservations) {
            Payment payment = paymentRepository.findByReservation(reservation).orElse(null);
            if (payment == null) {
                continue;
            }
            hasPaymentHistory = true;

            if (payment.getStatus() == PaymentStatus.PAID) {
                paymentService.refundAndSave(payment.getId(), "콘서트 삭제로 인한 자동 환불");
            }
        }

        if (hasPaymentHistory) {
            transactionTemplate.executeWithoutResult(status ->
                    concertRepository.findById(id).ifPresent(Concert::suspend));
            return;
        }

        transactionTemplate.executeWithoutResult(status ->
                concertRepository.findById(id).ifPresent(this::processRemoval));
    }

    @Transactional
    public void processRemoval(Concert concert) {
        List<Reservation> reservations = reservationRepository.findAllByConcertSchedule_Concert(concert);

        List<Reservation> paidReservations = reservations.stream()
                .filter(r -> paymentRepository.findByReservation(r)
                        .map(p -> p.getStatus() == PaymentStatus.PAID)
                        .orElse(false))
                .toList();

        if (paidReservations.isEmpty()) {
            reservationSeatRepository.deleteAllByReservationIn(reservations);
            reservationRepository.deleteAll(reservations);
            List<ConcertSchedule> schedules = concertScheduleRepository.findAllByConcertId(concert.getId());
            for (ConcertSchedule schedule : schedules) {
                scheduleSeatRepository.deleteAllByConcertSchedule(schedule);
            }
            concertScheduleRepository.deleteAll(schedules);
            concertSeatGradeRepository.deleteAllByConcert(concert);
            seatGradeRepository.deleteAllByConcert(concert);

            List<ConcertImage> images = concertImageRepository.findAllByConcertOrderBySortOrderAsc(concert);
            for (ConcertImage image : images) {
                imageUploadService.delete(image.getPublicId());
            }
            concertImageRepository.deleteAllByConcert(concert);
            concertRepository.delete(concert);
        } else {
            concert.suspend();
            for (Reservation reservation : paidReservations) {
                reservation.cancel();
                paymentRepository.findByReservation(reservation).ifPresent(Payment::refund);
            }
        }
    }
}