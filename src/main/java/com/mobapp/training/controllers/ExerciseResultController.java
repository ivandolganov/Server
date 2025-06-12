package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.CompleteExerciseRequest;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.logging.ExerciseResultLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.ExerciseResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exercise-results")
public class ExerciseResultController {

    private static final Logger logger = LoggerFactory.getLogger(ExerciseResultService.class);
    private final ExerciseResultService resultService;

    public ExerciseResultController(ExerciseResultService resultService) {
        this.resultService = resultService;
    }

    @PostMapping("/complete")
    public ResponseEntity<MessageResponse> complete(
            @RequestBody CompleteExerciseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ExerciseResultLogMessages.COMPLETE_EXERCISE_REQUEST,
                request.getExerciseId(), userDetails.getId());
        return ResponseEntity.ok(resultService.saveResult(request, userDetails));
    }
}

