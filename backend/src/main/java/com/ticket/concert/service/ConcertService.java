package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ConcertResponse;
import com.ticket.concert.dto.ConcertUpdateRequest;
import com.ticket.concert.dto.ImageInfo;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
        return concertRepository.findAllByCreateBy(user).stream()
                .map(concert -> {
                    List<ImageInfo> images = getImages(concert);
                    return new ConcertResponse(
                            concert.getId(), concert.getTitle(), concert.getDescription(), concert.getImageUrl(),
                            concert.getStatus(), concert.getSalesStartAt(), concert.getSalesEndAt(), images
                    );
                }).toList();
    }

    public ConcertResponse findConcert(Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));
        return new ConcertResponse(concert.getId(), concert.getTitle(),
                concert.getDescription(), concert.getImageUrl(),
                concert.getStatus(), concert.getSalesStartAt(), concert.getSalesEndAt(),
                getImages(concert));
    }

    public List<ConcertResponse> findAll() {
        return concertRepository.findAll().stream()
                .filter(c -> c.getStatus() != ConcertStatus.SUSPENDED)
                .map(c -> new ConcertResponse(c.getId(), c.getTitle(), c.getDescription(), c.getImageUrl(),
                        c.getStatus(), c.getSalesStartAt(), c.getSalesEndAt(), getImages(c)))
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

    @Transactional
    public void delete(Long userId, Long id) {
        Concert concert = concertRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_NOT_FOUND));

        if (!concert.getCreateBy().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        List<Reservation> reservations = reservationRepository.findAllByConcertSchedule_Concert(concert);
        List<Reservation> paidReservations = reservations.stream()
                .filter(r -> paymentRepository.findByReservation(r)
                        .map(p -> p.getStatus() == PaymentStatus.PAID)
                        .orElse(false))
                .toList();

        if (!paidReservations.isEmpty()) {
            for (Reservation r : paidReservations) {
                Payment payment = paymentRepository.findByReservation(r).get();
                paymentService.cancel(payment, "콘서트 삭제로 인한 자동 환불");
            }
            concert.suspend();
            return;
        }

        processRemoval(concert);
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
            List<ConcertSchedule> schedules = concertScheduleRepository.findAllByConcertId(concert.getId());
            for (ConcertSchedule schedule : schedules) {
                scheduleSeatRepository.deleteAllByConcertSchedule(schedule);
            }
            concertScheduleRepository.deleteAll(schedules);
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