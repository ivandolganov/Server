package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.EditWordThemeRequest;
import com.mobapp.training.dto.request.ThemeIdRequest;
import com.mobapp.training.dto.request.WordThemeRequest;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.ThemesResponse;
import com.mobapp.training.logging.ThemeLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.ThemeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/themes")
@PreAuthorize("hasRole('TEACHER')")
public class ThemeController {

    private static final Logger logger = LoggerFactory.getLogger(ThemeController.class);
    private final ThemeService themeService;

    @PostMapping("/create-theme")
    public MessageResponse createThemeWithWords(
            @RequestBody WordThemeRequest request) {
        logger.info(ThemeLogMessages.CREATE_THEME_REQUEST,
                request.getTitle(), request.getWords().size());
        return themeService.createThemeWithWords(request);
    }

    @GetMapping()
    public ThemesResponse getThemesWithWords(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return themeService.getThemesWithWords(userDetails.getId());
    }

    @PutMapping("/edit-theme")
    public MessageResponse editThemeWithWords(
            @RequestBody EditWordThemeRequest request
    ) {
        logger.info(ThemeLogMessages.EDIT_THEME_REQUEST, request.getThemeId());
        return themeService.editThemeWithWords(request);
    }

    @DeleteMapping("/delete-theme")
    public MessageResponse deleteThemeWithWords(
            @RequestBody ThemeIdRequest request) {
        logger.info(ThemeLogMessages.DELETE_THEME_REQUEST, request.getThemeId());
        return themeService.deleteThemeWithWords(request);
    }
}