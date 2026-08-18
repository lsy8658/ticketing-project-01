package com.ticket.concert.service;

import com.ticket.concert.domain.*;
import com.ticket.concert.repository.*;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertScheduleRepository concertScheduleRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final RedissonClient redissonClient;

    public Long create(Long userId, Long concertScheduleId, List<Long> scheduleSeatIds) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ConcertSchedule concertSchedule = concertScheduleRepository.findById(concertScheduleId)
                .orElseThrow(() -> new RuntimeException("공연 회차를 찾을 수 없습니다."));

        scheduleSeatIds.sort(Long::compareTo);

        List<RLock> locks = scheduleSeatIds.stream()
                .map(id -> redissonClient.getLock("seat:" + id))
                .toList();

        System.out.println("락 획득 시도");
        locks.forEach(RLock::lock);
        System.out.println("락 획득 완료");
        try {
            Reservation reservation = new Reservation(user, concertSchedule);
            Reservation savedReservation = reservationRepository.save(reservation);

            List<ScheduleSeat> scheduleSeats =
                    scheduleSeatRepository.findAllById(scheduleSeatIds);

            List<ReservationSeat> reservationSeats = scheduleSeats.stream()
                    .map(scheduleSeat ->
                            new ReservationSeat(savedReservation, scheduleSeat))
                    .toList();

            reservationSeatRepository.saveAll(reservationSeats);

            return savedReservation.getId();

        } finally {
            locks.forEach(RLock::unlock);
            System.out.println("락 해제 완료");
        }
    }
}