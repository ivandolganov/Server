package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "user_word_theme_results")
public class WordThemeResult {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private WordTheme theme;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Column(name = "result_time")
    private double resultTime;

    @Column(name = "attempted_at")
    private LocalDateTime attemptedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}