package com.mobapp.training.controllers;

import com.mobapp.training.dto.response.DateExerciseParamsResponse;
import com.mobapp.training.logging.DateExerciseLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.DateExerciseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/date-series")
public class DateExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(DateExerciseController.class);
    private final DateExerciseService dateExerciseService;

    @GetMapping("/{exerciseId}/start")
    public ResponseEntity<DateExerciseParamsResponse> startDateExercise(
            @PathVariable UUID exerciseId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        logger.info(DateExerciseLogMessages.START_DATE_EXERCISE_REQUEST, exerciseId);
        DateExerciseParamsResponse response = dateExerciseService.generateDateExercise(exerciseId, user.getUser());
        return ResponseEntity.ok(response);
    }
}
