package com.ticket.concert.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Venue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String managerPhone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "create_by", nullable = false)
    private User createBy;

    public Venue(String name, String address, int capacity, String managerPhone, User createBy) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.managerPhone = managerPhone;
        this.createBy = createBy;
    }

    public void update(String name, String address, int capacity, String managerPhone) {
        this.name = name;
        this.address = address;
        this.capacity = capacity;
        this.managerPhone = managerPhone;
    }
}