package com.ticket.concert.service;

import com.ticket.concert.domain.*;
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
            throw new RuntimeException("좌석은 최대 4매까지 예약 가능합니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ConcertSchedule concertSchedule = concertScheduleRepository.findById(concertScheduleId)
                .orElseThrow(() -> new RuntimeException("공연 회차를 찾을 수 없습니다."));

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
                    throw new RuntimeException("해당 공연 회차의 좌석이 아닙니다.");
                }

                if (scheduleSeat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new RuntimeException("이미 선택된 좌석이 있습니다.");
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
                .orElseThrow(() -> new RuntimeException("좌석을 찾을 수 없습니다."));

        scheduleSeat.release();
    }

    public void cancelReservation(Long reservationId, Long userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        if (!reservation.getUser().getId().equals(userId)) {
            throw new RuntimeException("본인 예약만 취소할 수 있습니다.");
        }

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new RuntimeException("이미 취소된 예약입니다.");
        }

        reservation.cancel();
    }
}