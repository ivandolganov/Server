package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "modules")
public class Modules {

    @Id
    @Getter
    private UUID id;

    @Getter @Setter
    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @Getter @Setter
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Getter @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter @Setter
    @Column(name = "end_date")
    private LocalDate endDate;

    @Getter @Setter
    @Column(name = "pass_threshold")
    private Integer passThreshold;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY)
    private List<Exercise> exercises; // или Set<Exercise>

    public List<Exercise> getExercises() {
        return exercises;
    }

    public Modules() {
    }

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}