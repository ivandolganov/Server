package com.mobapp.training.repo;

import com.mobapp.training.models.DateExerciseParams;
import com.mobapp.training.models.Exercise;
import com.mobapp.training.models.NamesFacesExerciseParams;
import com.mobapp.training.models.NumberExerciseParams;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface NamesFacesExerciseParamsRepository extends JpaRepository<NamesFacesExerciseParams, UUID> {
    Optional<NamesFacesExerciseParams> findByExercise(Exercise ex);

    Optional<NamesFacesExerciseParams> findByExerciseId(UUID exerciseId);

    void deleteAllByExercise(Exercise exercise);

    int deleteByExercise(Exercise exercise);
}
