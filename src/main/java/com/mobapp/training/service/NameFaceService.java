package com.mobapp.training.service;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.*;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.NameFaceLogMessages;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class NameFaceService {

    private static final Logger logger = LoggerFactory.getLogger(NameFaceService.class);
    private final NamesFacesExerciseParamsRepository nameFaceRepository;
    private final NameRepository nameRepository;

    public NameFaceResponse getRandomNames(UUID exerciseId) {
        logger.info(NameFaceLogMessages.GET_NAMES_START, exerciseId);

        NamesFacesExerciseParams params = nameFaceRepository.findByExerciseId(exerciseId)
                .orElseThrow(() -> {
                    logger.error(NameFaceLogMessages.EXERCISE_NOT_FOUND, exerciseId);
                    return new NotFoundException("Exercise not found");
                });

        String format = params.getNameFormat();
        int count = params.getNameCount();
        List<NameEntity> names = nameRepository.findRandomNamesByFormat(format, count);

        List<String> result = names.stream()
                .map(NameEntity::getName)
                .toList();

        logger.info(NameFaceLogMessages.GET_NAMES_SUCCESS,
                exerciseId, result.size(), format);

        NameFaceResponse response = new NameFaceResponse();
        response.setNames(result);
        return response;
    }

    public NameFaceUserResponse getRandomNamesUser(NameFaceRequest request) {
        logger.info(NameFaceLogMessages.GENERATE_NAMES_USER_START,
                request.getFormat(), request.getNameCount());

        request.setFormat(request.getFormat());
        List<NameEntity> names = nameRepository.findRandomNamesByFormat(request.getFormat(), request.getNameCount());

        List<String> result = names.stream()
                .map(NameEntity::getName)
                .toList();


        logger.info(NameFaceLogMessages.GENERATE_NAMES_USER_SUCCESS,
                request.getFormat(), result.size());

        NameFaceUserResponse response = new NameFaceUserResponse();
        response.setNames(result);
        return response;
    }


}
