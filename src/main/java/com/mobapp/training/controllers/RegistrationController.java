package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.EmailCheckRequest;
import com.mobapp.training.dto.request.GroupCheckRequest;
import com.mobapp.training.dto.request.RegistrationRequest;
import com.mobapp.training.dto.response.EmailCheckResponse;
import com.mobapp.training.dto.response.RegistrationResponse;
import com.mobapp.training.logging.RegistrationLogMessages;
import com.mobapp.training.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/check-email")
    public ResponseEntity<EmailCheckResponse> checkEmail(@RequestBody EmailCheckRequest request) {
        logger.info(RegistrationLogMessages.CHECK_EMAIL_REQUEST, request.getEmail());
        return ResponseEntity.ok(registrationService.checkEmail(request));
    }

    @PostMapping("/check-group")
    public ResponseEntity<?> checkGroup(@RequestBody GroupCheckRequest request) {
        logger.info(RegistrationLogMessages.CHECK_GROUP_REQUEST, request.getGroupCode());
        boolean exists = registrationService.checkGroupExists(request);
        return ResponseEntity.ok(Collections.singletonMap("exists", exists));
    }

    @PostMapping("/register-final")
    public ResponseEntity<RegistrationResponse> registerFinal(@RequestBody RegistrationRequest request) {
        logger.info(RegistrationLogMessages.REGISTER_REQUEST,
                request.getEmail(), request.getFirstName());
        return ResponseEntity.ok(registrationService.registerUser(request));
    }
}
