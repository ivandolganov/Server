package com.mobapp.training.repo;

import com.mobapp.training.models.UserWorkoutDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface UserWorkoutDateRepository extends JpaRepository<UserWorkoutDate, UUID> {
    boolean existsByUserIdAndWorkoutDate(UUID userId, LocalDate workoutDate);
    
    List<UserWorkoutDate> findByUserIdOrderByWorkoutDateDesc(UUID userId);
    
    @Modifying
    @Query("DELETE FROM UserWorkoutDate uw WHERE uw.userId = :userId AND uw.workoutDate = :workoutDate")
    void deleteByUserIdAndDate(@Param("userId") UUID userId,
                             @Param("workoutDate") LocalDate workoutDate);
}