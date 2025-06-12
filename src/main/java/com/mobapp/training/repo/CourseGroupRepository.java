package com.mobapp.training.repo;

import com.mobapp.training.models.CourseGroup;
import com.mobapp.training.models.Course;
import com.mobapp.training.models.StudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface CourseGroupRepository extends JpaRepository<CourseGroup, UUID> {
    List<CourseGroup> findByGroup(StudentGroup group);
    List<CourseGroup> findByCourse(Course course);
    Optional<CourseGroup> findByCourseAndGroup(Course course, StudentGroup group);
    boolean existsByCourseIdAndGroupIn(UUID courseId, Set<StudentGroup> groups);
    void deleteAllByCourse(Course course);

    int deleteByGroupId(UUID groupId);

    int deleteAllByGroupId(UUID groupId);

    @Query("SELECT DISTINCT cg.group.id FROM CourseGroup cg")
    Set<UUID> findAllGroupIdsInAnyCourse();

    @Query(value = "SELECT course_id FROM courses_groups WHERE group_id IN :groupIds", nativeQuery = true)
    Set<UUID> findCourseIdsByGroupIds(@Param("groupIds") Set<UUID> groupIds);
}
