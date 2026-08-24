package com.ticket.concert.config;

import com.ticket.concert.service.ReservationService;
import jakarta.annotation.PostConstruct;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener implements MessageListener {

    private final RedisMessageListenerContainer listenerContainer;
    private final ReservationService reservationService;

    public RedisKeyExpirationListener(
            RedisMessageListenerContainer listenerContainer,
            ReservationService reservationService
    ) {
        this.listenerContainer = listenerContainer;
        this.reservationService = reservationService;
    }

    @PostConstruct
    public void register() {
        listenerContainer.addMessageListener(this, new PatternTopic("__keyevent@*__:expired"));
        System.out.println("Redis 만료 Listener 등록됨 (직접 구독 방식)");
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());
        System.out.println("Redis 만료 감지 : " + expiredKey);

        if (!expiredKey.startsWith("seat:hold:")) {
            return;
        }

        Long scheduleSeatId = Long.parseLong(expiredKey.replace("seat:hold:", ""));
        reservationService.release(scheduleSeatId);
    }
}

