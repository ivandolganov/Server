package com.mobapp.training.service;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.SettingsResponse;
import com.mobapp.training.dto.response.WordExerciseTestQuestionResponse;
import com.mobapp.training.dto.response.WordItemsResponse;
import com.mobapp.training.enums.EnumUtils;
import com.mobapp.training.enums.Gender;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.WordSetExerciseLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkoutDateService {
    private final UserWorkoutDateRepository workoutDateRepository;

    @Transactional
    public MessageResponse addWorkoutDate(UUID userId, LocalDate workoutDate) {
        if (!workoutDateRepository.existsByUserIdAndWorkoutDate(userId, workoutDate)) {
            UserWorkoutDate workoutDateEntity = new UserWorkoutDate();
            workoutDateEntity.setUserId(userId);
            workoutDateEntity.setWorkoutDate(workoutDate);
            workoutDateRepository.save(workoutDateEntity);
        }
        return new MessageResponse("Дата тренировки добавлена!");
    }

    @Transactional
    public List<LocalDate> getUserWorkoutDates(UUID userId) {
        return workoutDateRepository.findByUserIdOrderByWorkoutDateDesc(userId).stream()
                .map(UserWorkoutDate::getWorkoutDate)
                .collect(Collectors.toList());
    }
}