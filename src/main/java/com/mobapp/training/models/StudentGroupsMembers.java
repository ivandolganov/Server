package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Entity
@Table(name = "student_group_members")
public class StudentGroupsMembers {

    @Id
    private UUID id;

    @ManyToOne
    @Setter
    @JoinColumn(name = "group_id", nullable = false)
    private StudentGroup group;

    @ManyToOne
    @Setter
    @JoinColumn(name = "user_id", nullable = false)
    private Users student;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
