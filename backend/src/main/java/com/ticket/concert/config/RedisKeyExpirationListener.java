package com.ticket.concert.config;

import com.ticket.concert.service.ReservationService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Slf4j
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
        try {
            listenerContainer.addMessageListener(this, new PatternTopic("__keyevent@*__:expired"));
            log.info("Redis 만료 Listener 등록됨");
        } catch (Exception e) {
            log.warn("Redis 연결 실패로 만료 Listener 등록 못함, 앱은 정상 기동함: {}", e.getMessage());
        }
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = new String(message.getBody());

        if (!expiredKey.startsWith("seat:hold:")) {
            return;
        }

        try {
            Long scheduleSeatId = Long.parseLong(expiredKey.replace("seat:hold:", ""));
            reservationService.release(scheduleSeatId);
        } catch (Exception e) {
            log.warn("만료 좌석 해제 실패 key={}", expiredKey, e);
        }
    }
}