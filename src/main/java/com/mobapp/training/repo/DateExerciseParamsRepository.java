package com.mobapp.training.repo;

import com.mobapp.training.models.CourseGroup;
import com.mobapp.training.models.DateExerciseParams;
import com.mobapp.training.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;

public interface DateExerciseParamsRepository extends JpaRepository<DateExerciseParams, UUID> {
    Optional<DateExerciseParams> findByExercise(Exercise ex);

    Optional<DateExerciseParams> findByExerciseId(UUID exerciseId);

    void deleteAllByExercise(Exercise exercise);

    int deleteByExercise(Exercise exercise);
}
