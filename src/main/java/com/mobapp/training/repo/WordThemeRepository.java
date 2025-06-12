package com.mobapp.training.repo;

import com.mobapp.training.models.Users;
import com.mobapp.training.models.WordTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WordThemeRepository extends JpaRepository<WordTheme, UUID> {
    List<WordTheme> findByUser(Users user);
}