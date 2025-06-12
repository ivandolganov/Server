package com.mobapp.training.repo;

import com.mobapp.training.models.StudentGroupsMembers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudentGroupMemberRepository extends JpaRepository<StudentGroupsMembers, UUID> {
    // Найти все записи для группы
    List<StudentGroupsMembers> findByGroupId(UUID groupId);

    // Найти все записи для студента
    List<StudentGroupsMembers> findByStudentId(UUID studentId);

    // Количество студентов в группе
    int countByGroupId(UUID groupId);

    int deleteByGroupId(UUID groupId);

    int deleteAllByGroupId(UUID groupId);
}
