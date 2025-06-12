package com.mobapp.training.repo;

import com.mobapp.training.models.Course;
import com.mobapp.training.models.StudentGroup;
import com.mobapp.training.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    @Query("SELECT c FROM Course c JOIN CourseGroup gc ON gc.course.id = c.id WHERE gc.group.id = :groupId")
    List<Course> findByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT c FROM Course c JOIN Modules m ON m.course.id = c.id WHERE m.id = :moduleId")
    Optional<Course> findByModuleId(@Param("moduleId") UUID moduleId);
    boolean existsByIdAndTeacherId(UUID courseId, UUID teacherId);

    List<Course> findByTeacher(Users teacher);

    List<Course> findByTeacherId(UUID teacherId);


}

