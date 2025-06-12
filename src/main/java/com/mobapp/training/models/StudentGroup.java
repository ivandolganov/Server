package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "student_groups")
public class StudentGroup {
    @Id
    @Getter
    @Column(name = "id", nullable = false)
    private UUID id;

    @Getter
    @Setter
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Getter
    @Setter
    @Column(name = "name")
    private String name;

    @ManyToMany
    @Getter
    @Setter
    @JoinTable(
            name = "student_group_members",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> students = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @Getter
    @Setter
    private Users creator;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }

}