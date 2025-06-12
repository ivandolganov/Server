package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.NameFaceRequest;
import com.mobapp.training.dto.response.NameFaceResponse;
import com.mobapp.training.dto.response.NameFaceUserResponse;
import com.mobapp.training.dto.response.NotificationResponse;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.NameFaceLogMessages;
import com.mobapp.training.models.ModuleNotification;
import com.mobapp.training.models.StudentGroup;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.NameFaceService;
import com.mobapp.training.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getAllNotifications(userDetails.getId()));
    }
}
