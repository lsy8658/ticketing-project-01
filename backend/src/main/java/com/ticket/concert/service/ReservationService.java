package com.ticket.concert.service;

import com.ticket.concert.domain.Concert;
import com.ticket.concert.domain.Reservation;
import com.ticket.concert.domain.User;
import com.ticket.concert.repository.ConcertRepository;
import com.ticket.concert.repository.ReservationRepository;
import com.ticket.concert.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ConcertRepository concertRepository;

    public Long create(Long userId, Long concertId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        Concert concert = concertRepository.findById(concertId)
                .orElseThrow(() -> new RuntimeException("공연을 찾을 수 없습니다."));

        Reservation reservation = new Reservation(user, concert);

        return reservationRepository.save(reservation).getId();
    }
}
