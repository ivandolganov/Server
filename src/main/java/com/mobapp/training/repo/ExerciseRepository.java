package com.mobapp.training.repo;

import com.mobapp.training.models.Domains;
import com.mobapp.training.models.Exercise;
import com.mobapp.training.models.Modules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, UUID> {
    List<Exercise> findByModule(Modules module);

    List<Exercise> findByModuleId(UUID id);

    List<Exercise> findByModuleAndType(Modules module, Exercise.ExerciseType type);

    void deleteAllByModule(Modules module);

    int deleteByModule(Modules module);
}
