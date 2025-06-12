package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@RequiredArgsConstructor
@Table(name = "user_words")
public class UserWord {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "theme_id")
    private WordTheme theme;

    private String term;
    private String translation;
    private String transcription;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}