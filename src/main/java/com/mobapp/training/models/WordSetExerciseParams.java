package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "word_set_exercise_params")
public class WordSetExerciseParams {
    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    private int questionCount;
    private boolean instantFeedback;

    @Enumerated(EnumType.STRING)
    private AnswerType answerType;

    public enum AnswerType {
        TERM, DEFINITION
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}

