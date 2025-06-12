package com.mobapp.training.repo;

import com.mobapp.training.models.Exercise;
import com.mobapp.training.models.NumberExerciseParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NumberExerciseParamsRepository extends JpaRepository<NumberExerciseParams, UUID> {
    Optional<NumberExerciseParams> findByExercise(Exercise ex);

    Optional<NumberExerciseParams> findByExerciseId(UUID exerciseId);

    void deleteAllByExercise(Exercise exercise);

    int deleteByExercise(Exercise exercise);
}
