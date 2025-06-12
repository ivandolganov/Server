package com.mobapp.training.service;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.enums.EnumUtils;
import com.mobapp.training.enums.Gender;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.NumberExerciseLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class NumberExerciseService {

    private static final Logger logger = LoggerFactory.getLogger(NumberExerciseService.class);
    private final NumberExerciseParamsRepository paramsRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseResultRepository exerciseResultRepository;

    public NumberExerciseParamsResponse generateExercise(UUID exerciseId, Users user) {
        logger.info(NumberExerciseLogMessages.GENERATE_EXERCISE_START,
                exerciseId, user.getId());

        NumberExerciseParams params = paramsRepository.findByExerciseId(exerciseId)
                .orElseThrow(() -> {
                    logger.error(NumberExerciseLogMessages.PARAMS_NOT_FOUND, exerciseId);
                    return new NotFoundException("Exercise not found");
                });

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> {
                    logger.error(NumberExerciseLogMessages.EXERCISE_NOT_FOUND, exerciseId);
                    return new NotFoundException("Exercise not found");
                });

        NumberExerciseParamsResponse resp = new NumberExerciseParamsResponse();
        resp.setExerciseId(exerciseId);
        resp.setBase(params.getBase());
        resp.setNumberCount(params.getNumberCount());
        resp.setDigit_lenth(params.getDigitLength());
        resp.setMaxValue(params.getMaxValue());
        resp.setMinValue(params.getMinValue());
        resp.setDisplayTimeMs(params.getDisplayTimeMs());
        resp.setLastResultPercent((int)getExerciseResultPercent(exercise, user));

        logger.info(NumberExerciseLogMessages.GENERATE_EXERCISE_SUCCESS,
                exerciseId, user.getId());

        return resp;
    }

    private double getExerciseResultPercent(Exercise exercise, Users student) {
        return exerciseResultRepository
                .findTopByExerciseAndStudentOrderByAttemptedAtDesc(exercise, student)
                .map(r -> (double) r.getCorrectCount() / r.getTotalCount() * 100.0)
                .orElse(0.0);
    }
}

