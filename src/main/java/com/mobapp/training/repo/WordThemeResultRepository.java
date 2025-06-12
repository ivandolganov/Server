package com.mobapp.training.repo;

import com.mobapp.training.models.Users;
import com.mobapp.training.models.WordTheme;
import com.mobapp.training.models.WordThemeResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WordThemeResultRepository extends JpaRepository<WordThemeResult, UUID> {
    List<WordThemeResult> findByUserAndTheme(Users user, WordTheme theme);
    Optional<WordThemeResult> findTopByUserAndThemeOrderByAttemptedAtDesc(Users user, WordTheme theme);

    Optional<WordThemeResult> findTopByUserAndThemeOrderByResultTimeDesc(Users user, WordTheme theme);
}