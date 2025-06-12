package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.NameFaceRequest;
import com.mobapp.training.dto.response.NameFaceResponse;
import com.mobapp.training.dto.response.NameFaceUserResponse;
import com.mobapp.training.logging.NameFaceLogMessages;
import com.mobapp.training.service.NameFaceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/name-face")
public class NameFaceController {

    private static final Logger logger = LoggerFactory.getLogger(NameFaceController.class);
    private final NameFaceService nameFaceService;

    public NameFaceController(NameFaceService nameFaceService) {
        this.nameFaceService = nameFaceService;
    }

    @GetMapping("/{exerciseId}/test")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<NameFaceResponse> generate(
            @PathVariable UUID exerciseId
    ) {
        logger.info(NameFaceLogMessages.GET_RANDOM_NAMES_REQUEST, exerciseId);
        NameFaceResponse response = nameFaceService.getRandomNames(exerciseId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<NameFaceUserResponse> generate(
            @RequestBody NameFaceRequest request
    ) {
        logger.info(NameFaceLogMessages.GENERATE_NAMES_REQUEST,
                request.getFormat(), request.getNameCount());
        NameFaceUserResponse response = nameFaceService.getRandomNamesUser(request);
        return ResponseEntity.ok(response);
    }
}
