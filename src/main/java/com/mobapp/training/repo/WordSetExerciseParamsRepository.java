package com.mobapp.training.repo;

import com.mobapp.training.models.Exercise;
import com.mobapp.training.models.NumberExerciseParams;
import com.mobapp.training.models.WordSetExerciseParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WordSetExerciseParamsRepository extends JpaRepository<WordSetExerciseParams, UUID> {
    Optional<WordSetExerciseParams> findByExercise(Exercise ex);

    Optional<WordSetExerciseParams> findByExerciseId(UUID exerciseId);

    void deleteAllByExercise(Exercise exercise);

    int deleteByExercise(Exercise exercise);
}
