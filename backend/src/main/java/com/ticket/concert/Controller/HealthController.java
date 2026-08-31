package com.ticket.concert.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HealthController {

    private final JdbcTemplate jdbcTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    @GetMapping("/health")
    public String health() {
        // DB 깨우기
        jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        // Redis 깨우기
        try (RedisConnection connection =
                     stringRedisTemplate.getConnectionFactory().getConnection()) {
            connection.ping();
        }

        return "OK";
    }
}