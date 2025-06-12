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
import com.mobapp.training.repo.GroupRepository;
import com.mobapp.training.repo.UsersRepository;
import com.mobapp.training.repo.WordItemsRepository;
import com.mobapp.training.repo.WordSetExerciseParamsRepository;
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
public class WordSetExerciseService {

    private static final Logger log = LoggerFactory.getLogger(WordSetExerciseService.class);
    private final WordSetExerciseParamsRepository paramsRepository;
    private final WordItemsRepository wordItemRepository;

    public List<WordItemsResponse> getWords(UUID exerciseId) {
        log.info(WordSetExerciseLogMessages.SERVICE_GET_TRAINING_START, exerciseId);

        WordSetExerciseParams params = paramsRepository.findByExerciseId(exerciseId)
                .orElseThrow(() -> {
                    log.error(WordSetExerciseLogMessages.SERVICE_GET_TRAINING_PARAMS_NOT_FOUND, exerciseId);
                    return new NotFoundException("Параметры упражнения не найдены");
                });

        Theme theme = params.getTheme();

        return wordItemRepository.findAllByTheme(theme).stream()
                .map(w -> {
                    WordItemsResponse resp = new WordItemsResponse();
                    resp.setWord(w.getWord());
                    resp.setTranscription(w.getTranscription());
                    resp.setTranslation(w.getTranslation());
                    return resp;
                })
                .collect(Collectors.toList());
    }
}

