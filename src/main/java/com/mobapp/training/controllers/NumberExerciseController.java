package com.mobapp.training.controllers;

import com.mobapp.training.dto.response.NumberExerciseParamsResponse;
import com.mobapp.training.logging.NumberExerciseLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.NumberExerciseService;
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
@RequestMapping("/api/number-series")
public class NumberExerciseController {

    private static final Logger logger = LoggerFactory.getLogger(NumberExerciseController.class);
    private final NumberExerciseService numberExerciseService;

    @GetMapping("/{exerciseId}/start")
    public ResponseEntity<NumberExerciseParamsResponse> startExercise(
            @PathVariable UUID exerciseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(NumberExerciseLogMessages.START_EXERCISE_REQUEST,
                exerciseId, userDetails.getId());
        NumberExerciseParamsResponse response = numberExerciseService.generateExercise(exerciseId, userDetails.getUser());
        return ResponseEntity.ok(response);
    }
}

