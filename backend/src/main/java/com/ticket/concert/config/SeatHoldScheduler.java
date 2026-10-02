package com.ticket.concert.config;


import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.domain.SeatStatus;
import com.ticket.concert.repository.ScheduleSeatRepository;
import com.ticket.concert.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatHoldScheduler {
    private static final int EXPIRE_MINUTES = 2;

    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ReservationService reservationService;

    @Scheduled(fixedDelay = 60000)
    public void releaseExpiredSeats() {
        LocalDateTime limit = LocalDateTime.now().minusMinutes(EXPIRE_MINUTES);

        List<ScheduleSeat> expiredSeats = scheduleSeatRepository
                .findAllByStatusAndHoldAtBefore(SeatStatus.HOLDING, limit);

        for (ScheduleSeat seat : expiredSeats) {
            try {
                reservationService.release(seat.getId());
            } catch (Exception e) {
                log.warn("좌석 해제 실패 id = {}", seat.getId(), e);
            }
        }
    }
}
