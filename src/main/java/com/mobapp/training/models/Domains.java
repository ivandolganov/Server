package com.mobapp.training.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "email_domains")
@AllArgsConstructor
@NoArgsConstructor
public class Domains {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Getter
    @Setter
    @Column(name = "domain")
    private String domain;

    @Getter
    @Setter
    @Column(name = "university_name")
    private String universityName;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}
