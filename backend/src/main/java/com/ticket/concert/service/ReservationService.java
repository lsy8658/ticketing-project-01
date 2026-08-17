package com.ticket.concert.service;

import com.ticket.concert.domain.ConcertSchedule;
import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.User;
import com.ticket.concert.repository.ConcertScheduleRepository;
import com.ticket.concert.repository.ReservationRepository;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertScheduleRepository concertScheduleRepository;

    public Long create(Long userId, Long concertScheduleId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        ConcertSchedule concertSchedule = concertScheduleRepository.findById(concertScheduleId)
                .orElseThrow(() -> new RuntimeException("공연 회차를 찾을 수 없습니다."));

        Reservation reservation = new Reservation(user, concertSchedule);

        return reservationRepository.save(reservation).getId();
    }
}