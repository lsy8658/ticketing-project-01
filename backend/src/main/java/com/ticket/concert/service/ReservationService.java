package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.dto.ReservationDetailResponse;
import com.ticket.concert.dto.ReservationResponse;
import com.ticket.concert.exception.CustomException;
import com.ticket.concert.exception.ErrorCode;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final TransactionTemplate transactionTemplate;
    private static final int HOLD_MINUTES = 2;

    public Long create(Long userId, Long concertScheduleId, List<Long> scheduleSeatIds) {

        if (scheduleSeatIds.size() > 4) {
            throw new CustomException(ErrorCode.RESERVATION_SEAT_LIMIT_EXCEEDED);
        }

        scheduleSeatIds.sort(Long::compareTo);

        List<RLock> locks = scheduleSeatIds.stream()
                .map(id -> redissonClient.getLock("seat:" + id))
                .toList();

        List<RLock> acquiredLocks = new ArrayList<>();

        try {
            log.info("락 획득 시도");
            for (RLock lock : locks) {
                lock.lock();
                acquiredLocks.add(lock);
            }
            log.info("락 획득 완료");

            return transactionTemplate.execute(status -> {

                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

                ConcertSchedule concertSchedule = concertScheduleRepository.findById(concertScheduleId)
                        .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

                Concert concert = concertSchedule.getConcert();

                if (concert.getStatus() == ConcertStatus.SUSPENDED) {
                    throw new CustomException(ErrorCode.CONCERT_SUSPENDED);
                }

                LocalDateTime now = LocalDateTime.now();

                if (now.isBefore(concert.getSalesStartAt()) || now.isAfter(concert.getSalesEndAt())) {
                    throw new CustomException(ErrorCode.RESERVATION_SALES_NOT_OPEN);
                }

                if (now.isAfter(concertSchedule.getStartAt())) {
                    throw new CustomException(ErrorCode.CONCERT_ALREADY_STARTED);
                }

                long alreadyReserved = reservationSeatRepository.countByUserAndSchedule(
                        userId, concertScheduleId, ReservationStatus.RESERVED);

                if (alreadyReserved + scheduleSeatIds.size() > 4) {
                    throw new CustomException(ErrorCode.RESERVATION_SEAT_LIMIT_EXCEEDED);
                }

                Reservation reservation = new Reservation(user, concertSchedule);
                Reservation savedReservation = reservationRepository.save(reservation);

                List<ScheduleSeat> scheduleSeats =
                        scheduleSeatRepository.findAllById(scheduleSeatIds);

                if (scheduleSeats.size() != scheduleSeatIds.size()) {
                    throw new CustomException(ErrorCode.SCHEDULE_SEAT_NOT_FOUND);
                }

                for (ScheduleSeat scheduleSeat : scheduleSeats) {
                    if (!scheduleSeat.getConcertSchedule().getId().equals(concertScheduleId)) {
                        throw new CustomException(ErrorCode.SCHEDULE_SEAT_MISMATCH);
                    }

                    if (scheduleSeat.getStatus() != SeatStatus.AVAILABLE) {
                        throw new CustomException(ErrorCode.SEAT_ALREADY_TAKEN);
                    }
                }

                for (ScheduleSeat scheduleSeat : scheduleSeats) {
                    scheduleSeat.hold();

                    log.info("Redis 저장 : {}", scheduleSeat.getId());

                    redisTemplate.opsForValue().set(
                            "seat:hold:" + scheduleSeat.getId(),
                            "HOLD",
                            HOLD_MINUTES,
                            TimeUnit.MINUTES
                    );
                }

                List<ReservationSeat> reservationSeats = scheduleSeats.stream()
                        .map(scheduleSeat ->
                                new ReservationSeat(savedReservation, scheduleSeat))
                        .toList();

                reservationSeatRepository.saveAll(reservationSeats);

                return savedReservation.getId();
            });

        } finally {
            for (RLock lock : acquiredLocks) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
            log.info("락 해제 완료");
        }
    }
    @Transactional(readOnly = true)
    public ReservationDetailResponse getReservationDetail(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.RESERVATION_FORBIDDEN);
        }

        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        List<ReservationDetailResponse.SeatInfo> seatInfos = reservationSeats.stream()
                .map(rs -> new ReservationDetailResponse.SeatInfo(
                        rs.getScheduleSeat().getSeat().getSeatNumber(),
                        rs.getScheduleSeat().getSeatGrade().getName(),
                        rs.getScheduleSeat().getSeatGrade().getPrice()
                ))
                .toList();

        long totalAmount = seatInfos.stream()
                .mapToLong(ReservationDetailResponse.SeatInfo::getPrice).sum();

        return new ReservationDetailResponse(reservationId,seatInfos,totalAmount);
    }

    @Transactional
    public void release(Long scheduleSeatId) {
        ScheduleSeat scheduleSeat = scheduleSeatRepository.findById(scheduleSeatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_SEAT_NOT_FOUND));

        if (scheduleSeat.getStatus() != SeatStatus.HOLDING) {
            return;
        }

        scheduleSeat.release();

        reservationSeatRepository
                .findByScheduleSeatAndReservation_Status(scheduleSeat, ReservationStatus.RESERVED)
                .ifPresent(reservationSeat -> reservationSeat.getReservation().cancel());
    }

    @Transactional
    public void releaseHolding(Long reservationId, Long userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.RESERVATION_FORBIDDEN);
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || paymentRepository.existsByReservation(reservation)) {
            return;
        }

        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        for (ReservationSeat reservationSeat : reservationSeats) {
            ScheduleSeat scheduleSeat = reservationSeat.getScheduleSeat();
            if (scheduleSeat.getStatus() == SeatStatus.HOLDING) {
                scheduleSeat.release();
                redisTemplate.delete("seat:hold:" + scheduleSeat.getId());
            }
        }

        reservation.cancel();
    }

    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.RESERVATION_FORBIDDEN);
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
        }

        if (reservation.getConcertSchedule().getStartAt().isBefore(LocalDateTime.now().plusDays(1))) {
            throw new CustomException(ErrorCode.RESERVATION_CANCEL_DEADLINE_PASSED);
        }

        if (paymentRepository.existsByReservation(reservation)) {
            paymentService.cancelByReservation(reservation, "예약 취소");
        }

        reservation.cancel();

        List<ReservationSeat> reservationSeats =
                reservationSeatRepository.findAllByReservation(reservation);

        for (ReservationSeat reservationSeat : reservationSeats) {
            reservationSeat.getScheduleSeat().release();
        }
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getMyReservation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return reservationRepository.findAllByUser(user).stream()
                .map(ReservationResponse::from)
                .toList();
    }
}