package com.mobapp.training.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private UUID notificationId;
    private UUID moduleId;
    private String moduleName;
    private String courseName;
    private String teacherFullName;
    private String endDate;
}