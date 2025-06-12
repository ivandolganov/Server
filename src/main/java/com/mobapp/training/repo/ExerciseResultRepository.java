package com.mobapp.training.repo;

import com.mobapp.training.models.Exercise;
import com.mobapp.training.models.ExerciseResult;
import com.mobapp.training.models.Modules;
import com.mobapp.training.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ExerciseResultRepository extends JpaRepository<ExerciseResult, UUID> {
    Optional<ExerciseResult> findTopByExerciseAndStudentOrderByAttemptedAtDesc(Exercise exercise, Users student);

    List<ExerciseResult> findByExerciseInAndStudent(List<Exercise> exercises, Users user);

    void deleteAllByExercise(Exercise exercise);
}
