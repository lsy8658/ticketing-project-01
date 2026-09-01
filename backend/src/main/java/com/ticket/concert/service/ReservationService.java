package com.ticket.concert.service;

import com.ticket.concert.domain.*;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate redisTemplate;

    public Long create(Long userId, Long concertScheduleId, List<Long> scheduleSeatIds) {

        if (scheduleSeatIds.size() > 4) {
            throw new CustomException(ErrorCode.RESERVATION_SEAT_LIMIT_EXCEEDED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ConcertSchedule concertSchedule = concertScheduleRepository.findById(concertScheduleId)
                .orElseThrow(() -> new CustomException(ErrorCode.CONCERT_SCHEDULE_NOT_FOUND));

        scheduleSeatIds.sort(Long::compareTo);

        List<RLock> locks = scheduleSeatIds.stream()
                .map(id -> redissonClient.getLock("seat:" + id))
                .toList();

        log.info("락 획득 시도");
        locks.forEach(RLock::lock);
        log.info("락 획득 완료");

        try {
            Reservation reservation = new Reservation(user, concertSchedule);
            Reservation savedReservation = reservationRepository.save(reservation);

            List<ScheduleSeat> scheduleSeats =
                    scheduleSeatRepository.findAllById(scheduleSeatIds);

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
                log.info("Connection Factory : {}", redisTemplate.getConnectionFactory());

                redisTemplate.opsForValue().set(
                        "seat:hold:" + scheduleSeat.getId(),
                        "HOLD",
                        10,
                        TimeUnit.SECONDS
                );
            }

            List<ReservationSeat> reservationSeats = scheduleSeats.stream()
                    .map(scheduleSeat ->
                            new ReservationSeat(savedReservation, scheduleSeat))
                    .toList();

            reservationSeatRepository.saveAll(reservationSeats);

            return savedReservation.getId();

        } finally {
            locks.forEach(RLock::unlock);
            log.info("락 해제 완료");
        }
    }

    public void release(Long scheduleSeatId) {

        ScheduleSeat scheduleSeat = scheduleSeatRepository.findById(scheduleSeatId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHEDULE_SEAT_NOT_FOUND));

        scheduleSeat.release();
    }

    public void cancelReservation(Long reservationId, Long userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.RESERVATION_NOT_FOUND));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.RESERVATION_FORBIDDEN);
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new CustomException(ErrorCode.RESERVATION_ALREADY_CANCELLED);
        }

        reservation.cancel();
    }
}