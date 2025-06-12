package com.mobapp.training.repo;

import com.mobapp.training.enums.UserRole;
import com.mobapp.training.models.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsersRepository extends CrudRepository<Users, UUID> {
//    @EntityGraph(attributePaths = {"teacherCourses", "studentGroups"})

    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);
    Optional<Users> findByEmailAndPasswordHash(String email, String passwordHash);

    Optional<Users> findWithGroupsById(UUID studentId);
}
