package com.mobapp.training.controllers;

import com.mobapp.training.dto.response.WordExerciseTestQuestionResponse;
import com.mobapp.training.dto.response.WordItemsResponse;
import com.mobapp.training.logging.WordSetExerciseLogMessages;
import com.mobapp.training.service.WordSetExerciseService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/word-set-exercises")
@RequiredArgsConstructor
public class WordSetExerciseController {

    private static final Logger log = LoggerFactory.getLogger(WordSetExerciseController.class);
    private final WordSetExerciseService wordSetExerciseService;

    @GetMapping("/{exerciseId}/training")
    public List<WordItemsResponse> getTraining(@PathVariable UUID exerciseId) {
        log.info(WordSetExerciseLogMessages.GET_TRAINING_START, exerciseId);
        return wordSetExerciseService.getWords(exerciseId);
    }

    @GetMapping("/{exerciseId}/test")
    public List<WordItemsResponse> getTest(@PathVariable UUID exerciseId) {
        log.info(WordSetExerciseLogMessages.GET_TEST_START, exerciseId);
        return wordSetExerciseService.getWords(exerciseId);
    }
}
