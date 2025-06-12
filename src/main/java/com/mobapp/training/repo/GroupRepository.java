package com.mobapp.training.repo;

import com.mobapp.training.models.StudentGroup;
import com.mobapp.training.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<StudentGroup, UUID> {
    Optional<StudentGroup> findByCode(String code);
    Optional<StudentGroup> findById(UUID id);


    boolean existsByCode(String groupCode);

    Set<StudentGroup> findAllByIdIn(List<UUID> groupsId);

    List<StudentGroup> findByCreator(Users creator);

    @Query("SELECT COUNT(s) FROM StudentGroup g JOIN g.students s WHERE g.id = :groupId")
    int countStudentsByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT s FROM StudentGroup g JOIN g.students s WHERE g.id = :groupId")
    List<Users> findStudentsByGroupId(@Param("groupId") UUID groupId);

    @Query("SELECT cg.group FROM CourseGroup cg WHERE cg.course.id = :courseId")
    List<StudentGroup> findGroupsByCourseId(@Param("courseId") UUID courseId);


    Optional<StudentGroup> findWithStudentsById(UUID groupId);
}