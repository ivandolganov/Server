package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.LoginRequest;
import com.mobapp.training.service.LoginService;
import com.mobapp.training.logging.LoginLogMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        logger.info(LoginLogMessages.ATTEMPT_LOGIN, request.getEmail());

        return loginService.login(request)
                .<ResponseEntity<?>>map(response -> {
                    logger.info(LoginLogMessages.LOGIN_SUCCESS, request.getEmail());
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    logger.warn(LoginLogMessages.LOGIN_FAILURE, request.getEmail());
                    return ResponseEntity.status(401).body("Неверный email или пароль");
                });
    }
}
