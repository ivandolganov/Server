package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.*;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.SettingsResponse;
import com.mobapp.training.logging.SettingsLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.SettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SettingsController {

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);
    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PostMapping("/add-group")
    public ResponseEntity<MessageResponse> addGroup(
            @RequestBody AddGroupRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(SettingsLogMessages.ADD_GROUP_REQUEST,
                userDetails.getId(), request.getGroupCode());
        return ResponseEntity.ok(settingsService.addGroup(request, userDetails.getId()));
    }

    @PostMapping("/password-change")
    public ResponseEntity<MessageResponse> passwordChange(
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(SettingsLogMessages.PASSWORD_CHANGE_REQUEST, userDetails.getId());
        return ResponseEntity.ok(settingsService.passwordChange(request, userDetails));
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> settings(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info(SettingsLogMessages.GET_SETTINGS_REQUEST, userDetails.getId());
        return ResponseEntity.ok(settingsService.settings(userDetails));
    }

    @PostMapping("/settings-save")
    public ResponseEntity<?> settingsSave(
            @RequestBody SettingsRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(SettingsLogMessages.SAVE_SETTINGS_REQUEST, userDetails.getId());
        return ResponseEntity.ok(settingsService.settingsSave(request, userDetails));
    }
}
