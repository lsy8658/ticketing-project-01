package com.ticket.concert.service;

import com.ticket.concert.domain.ScheduleSeat;
import com.ticket.concert.repository.ScheduleSeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HoldExpirationService {
    private final ScheduleSeatRepository scheduleSeatRepository;

    public void release(Long scheduleSeatId) {
        ScheduleSeat scheduleSeat = scheduleSeatRepository.findById(scheduleSeatId)
                .orElseThrow();

        scheduleSeat.release();
    }
}
