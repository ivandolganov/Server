package com.mobapp.training.repo;

import com.mobapp.training.models.ModuleNotification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface ModuleNotificationRepository extends JpaRepository<ModuleNotification, UUID> {
    List<ModuleNotification> findByModule_Course_Id(UUID courseId);

    List<ModuleNotification> findByModuleCourseIdIn(Set<UUID> courseIds);
}