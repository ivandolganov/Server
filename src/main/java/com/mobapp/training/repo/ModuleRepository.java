package com.mobapp.training.repo;

import com.mobapp.training.models.Modules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuleRepository extends JpaRepository<Modules, UUID> {
    List<Modules> findByCourseId(UUID courseId);

    List<Modules> findWithExercisesByCourseId(UUID id);
}

