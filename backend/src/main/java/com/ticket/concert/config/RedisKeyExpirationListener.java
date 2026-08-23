package com.ticket.concert.config;

import com.ticket.concert.service.ReservationService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    private final ReservationService reservationService;

    public RedisKeyExpirationListener(
            RedisMessageListenerContainer listenerContainer,
            ReservationService reservationService
    ) {
        super(listenerContainer);
        this.reservationService = reservationService;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        System.out.println("Redis 만료 감지 : " + new String(message.getBody()));
        String expiredKey = new String(message.getBody());

        if (!expiredKey.startsWith("seat:hold:")) {
            return;
        }

        Long scheduleSeatId = Long.parseLong(
                expiredKey.replace("seat:hold:", "")
        );

        reservationService.release(scheduleSeatId);
    }
}