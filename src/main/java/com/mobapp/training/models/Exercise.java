package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "exercise")
public class Exercise {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "module_id", nullable = true)
    private Modules module; // может быть null, если пользовательский

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    @Enumerated(EnumType.STRING)
    private ExerciseType type; // DATE, NUMBER, NAME_FACE, WORD_SET

    private LocalDateTime createdAt;

    public Exercise(UUID uuid, Modules module, ExerciseType exerciseType) {
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    public enum ExerciseType {
        DATE,
        NUMBER,
        NAME_FACE,
        WORD_SET
    }

}
