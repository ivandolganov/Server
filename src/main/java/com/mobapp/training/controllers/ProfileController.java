package com.mobapp.training.controllers;

import com.mobapp.training.dto.response.ProfileResponse;
import com.mobapp.training.logging.ProfileLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.ProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> profile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ProfileLogMessages.GET_PROFILE_REQUEST, userDetails.getId());
        ProfileResponse responseList = profileService.profile(userDetails);
        return ResponseEntity.ok(responseList);
    }
}
