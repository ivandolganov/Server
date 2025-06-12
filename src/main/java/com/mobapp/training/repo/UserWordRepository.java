package com.mobapp.training.repo;

import com.mobapp.training.models.UserWord;
import com.mobapp.training.models.WordTheme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserWordRepository extends JpaRepository<UserWord, UUID> {
    List<UserWord> findByTheme(WordTheme theme);

    void deleteByTheme(WordTheme theme);
}