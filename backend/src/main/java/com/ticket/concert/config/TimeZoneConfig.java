package com.ticket.concert.config;

import jakarta.annotation.PostConstruct;

import java.util.TimeZone;

public class TimeZoneConfig {
    @PostConstruct
    public void setTimeZone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}
