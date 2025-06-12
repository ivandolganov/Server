package com.mobapp.training.service;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.enums.EnumUtils;
import com.mobapp.training.enums.Gender;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.DateExerciseLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DateExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(DateExerciseService.class);
    private final DateExerciseParamsRepository paramsRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseResultRepository exerciseResultRepository;

    public DateExerciseParamsResponse generateDateExercise(UUID exerciseId, Users user) {
        logger.info(DateExerciseLogMessages.GENERATE_DATE_EXERCISE_START, exerciseId, user.getId());

        DateExerciseParams params = paramsRepository.findByExerciseId(exerciseId)
                .orElseThrow(() -> {
                    logger.error(DateExerciseLogMessages.PARAMS_NOT_FOUND, exerciseId);
                    return new RuntimeException("Exercise parameters not found");
                });


        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> {
                    logger.error(DateExerciseLogMessages.EXERCISE_NOT_FOUND, exerciseId);
                    return new NotFoundException("Exercise not found");
                });

        DateExerciseParamsResponse response = new DateExerciseParamsResponse();
        response.setExerciseId(exerciseId);
        response.setDateCount(params.getDateCount());
        response.setDateRangeEnd(params.getDateRangeEnd());
        response.setDateRangeStart(params.getDateRangeStart());
        response.setFormat(params.getFormat().getDisplayName());
        response.setDisplayTimeMs(params.getDisplayTimeMs());
        response.setLastResultPercent((int)getExerciseResultPercent(exercise, user));
        logger.info(DateExerciseLogMessages.GENERATE_DATE_EXERCISE_SUCCESS, exerciseId, user.getId());
        return response;
    }

    private double getExerciseResultPercent(Exercise exercise, Users student) {
        return exerciseResultRepository
                .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student)
                .map(r -> (double) r.getCorrectCount() / r.getTotalCount() * 100.0)
                .orElse(0.0);
    }
}

