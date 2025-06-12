package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "number_exercise_params")
public class NumberExerciseParams {
    @Id
    private UUID id;

    @OneToOne
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    private int numberCount;
    private int base; // 2 или 10
    private Integer digitLength;
    private Integer minValue;
    private Integer maxValue;
    private int displayTimeMs;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}

