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
@Table(name = "user_word_themes")
public class WordTheme {
    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    private String title;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    private List<UserWord> words;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
    }
}