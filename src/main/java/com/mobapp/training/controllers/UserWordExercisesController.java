package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.ThemeIdRequest;
import com.mobapp.training.dto.request.UserWordRequest;
import com.mobapp.training.dto.request.WordThemeRequest;
import com.mobapp.training.dto.request.WordThemeResultRequest;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.UserThemeWordsResponse;
import com.mobapp.training.dto.response.WordThemeResponse;
import com.mobapp.training.logging.UserWordExercisesLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.UserWordExercisesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/word-themes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class UserWordExercisesController {

    private static final Logger logger = LoggerFactory.getLogger(UserWordExercisesController.class);
    private final UserWordExercisesService userWordExercisesService;

    @PostMapping("/theme-create")
    public ResponseEntity<UUID> createTheme(
            @RequestBody WordThemeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info(UserWordExercisesLogMessages.CREATE_THEME_REQUEST,
                userDetails.getId(), request.getTitle(), request.getWords().size());

        return ResponseEntity.ok(userWordExercisesService.createTheme(userDetails.getId(), request));
    }

    @PostMapping("/{themeId}/add-word")
    @PreAuthorize("@userThemeSecurity.isThemeOwner(#themeId, authentication.principal.id)")
    public ResponseEntity<MessageResponse> addWord(
            @PathVariable UUID themeId,
            @RequestBody UserWordRequest request
    ) {
        logger.info(UserWordExercisesLogMessages.ADD_WORDS_REQUEST,
                themeId, request.getWords().size());
        return ResponseEntity.ok(userWordExercisesService.addWordsToTheme(request, themeId));
    }

    @PostMapping("/my-themes/{themeId}/accordance/save-result")
    public ResponseEntity<MessageResponse> saveResult(
            @PathVariable UUID themeId,
            @RequestBody WordThemeResultRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        logger.info(UserWordExercisesLogMessages.SAVE_RESULT_REQUEST,
                userDetails.getId(), themeId, request.getResultTime());
        return ResponseEntity.ok(userWordExercisesService.saveResult(userDetails.getId(), themeId, request));
    }

    @GetMapping("/my-themes")
    public ResponseEntity<List<WordThemeResponse>> getThemes(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(UserWordExercisesLogMessages.GET_THEMES_REQUEST, userDetails.getId());
        return ResponseEntity.ok(userWordExercisesService.getUserThemes(userDetails.getId()));
    }

    @PreAuthorize("@userThemeSecurity.isThemeOwner(#themeId, authentication.principal.id)")
    @PutMapping("/my-themes/{themeId}/edit-theme")
    public ResponseEntity<MessageResponse> editTheme(
            @PathVariable UUID themeId,
            @RequestBody WordThemeRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        logger.info(UserWordExercisesLogMessages.EDIT_THEME_REQUEST,
                userDetails.getId(), themeId);
        return ResponseEntity.ok(userWordExercisesService.editTheme(themeId, userDetails.getId(), request));
    }

    @DeleteMapping("/my-themes/delete-theme")
    public ResponseEntity<MessageResponse> deleteTheme(
            @RequestBody ThemeIdRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(UserWordExercisesLogMessages.DELETE_THEME_REQUEST, request.getThemeId());
        return ResponseEntity.ok(userWordExercisesService.deleteTheme(request));
    }

    @PreAuthorize("@userThemeSecurity.isThemeOwner(#themeId, authentication.principal.id)")
    @GetMapping("/my-themes/{themeId}")
    public ResponseEntity<UserThemeWordsResponse> getTheme(
            @PathVariable UUID themeId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(UserWordExercisesLogMessages.GET_THEME_WORDS_REQUEST,
                userDetails.getId(), themeId);
        return ResponseEntity.ok(userWordExercisesService.getUsersThemeWords(themeId, userDetails.getId()));
    }
}
