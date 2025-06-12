package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.WorkoutDateRequest;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.WordExerciseTestQuestionResponse;
import com.mobapp.training.dto.response.WordItemsResponse;
import com.mobapp.training.logging.WordSetExerciseLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.WordSetExerciseService;
import com.mobapp.training.service.WorkoutDateService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/workout-dates")
@RequiredArgsConstructor
public class WorkoutDateController {
    private final WorkoutDateService workoutDateService;

    @PostMapping("/add-workout_date")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> addWorkoutDate(
            @RequestBody WorkoutDateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(workoutDateService.addWorkoutDate(userDetails.getId(), request.getWorkoutDate()));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LocalDate>> getUserWorkoutDates(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<LocalDate> dates = workoutDateService.getUserWorkoutDates(userDetails.getId());
        return ResponseEntity.ok(dates);
    }
}