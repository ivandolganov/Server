package com.mobapp.training.service;

import com.mobapp.training.dto.request.CompleteExerciseRequest;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.*;
import com.mobapp.training.logging.ExerciseResultLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import com.mobapp.training.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ExerciseResultService {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseResultService.class);
    private final ExerciseRepository exerciseRepository;
    private final UsersRepository usersRepository;
    private final ExerciseResultRepository resultRepository;

    public ExerciseResultService(ExerciseRepository exerciseRepository, UsersRepository usersRepository, ExerciseResultRepository resultRepository) {
        this.exerciseRepository = exerciseRepository;
        this.usersRepository = usersRepository;
        this.resultRepository = resultRepository;
    }

    public MessageResponse saveResult(CompleteExerciseRequest request, CustomUserDetails userDetails) {
        logger.info(ExerciseResultLogMessages.SAVE_RESULT_START,
                request.getExerciseId(), userDetails.getId());
        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> {
                    logger.error(ExerciseResultLogMessages.EXERCISE_NOT_FOUND, request.getExerciseId());
                    return new NotFoundException("Упражнение не найдено");
                });

        Users student = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(ExerciseResultLogMessages.USER_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });

        ExerciseResult result = new ExerciseResult();
        result.setExercise(exercise);
        result.setStudent(student);
        result.setCorrectCount(request.getCorrectAnswers());
        result.setTotalCount(request.getTotalQuestions());

        ExerciseResult saved = resultRepository.save(result);

        logger.info(ExerciseResultLogMessages.RESULT_SAVED,
                saved.getId(), exercise.getId(), student.getId(),
                request.getCorrectAnswers(), request.getTotalQuestions());

        logger.info(ExerciseResultLogMessages.SAVE_RESULT_SUCCESS,
                exercise.getId(), student.getId());

        return new MessageResponse("Результат успешно сохранен!");
    }
}

