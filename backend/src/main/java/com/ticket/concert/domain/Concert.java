package com.ticket.concert.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Concert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    private String description;
    private String imageUrl;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="create_by", nullable = false)
    private User createBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConcertStatus status;

    @Column(nullable = false)
    private LocalDateTime salesStartAt;

    @Column(nullable = false)
    private  LocalDateTime salesEndAt;

    @Builder
    public Concert (
            String title,
            String description,
            String imageUrl,
            User createBy,
            LocalDateTime salesStartAt,
            LocalDateTime salesEndAt
    ) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createBy = createBy;
        this.salesStartAt = salesStartAt;
        this.salesEndAt = salesEndAt;
        this.status = ConcertStatus.ACTIVE;
    }

    public void update(String title, String description, String imageUrl) {
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public void suspend () {
        this.status = ConcertStatus.SUSPENDED;
    }
}
