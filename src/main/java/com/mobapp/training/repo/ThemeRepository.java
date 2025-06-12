package com.mobapp.training.repo;

import com.mobapp.training.models.Theme;
import com.mobapp.training.models.Users;
import com.mobapp.training.models.WordSetExerciseParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, UUID> {
    List<Theme> findByCreatedBy(Users createdBy);
}