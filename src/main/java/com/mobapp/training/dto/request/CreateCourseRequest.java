package com.mobapp.training.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Schema(description = "DTO для создания нового курса")
@AllArgsConstructor
public class CreateCourseRequest {
    @Schema(
            description = "Название курса",
            example = "Основы Spring Boot",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String courseName;
    @Schema(
            description = "Список ID групп, для которых создается курс",
            example = "[1, 2, 3]",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private List<UUID> groupsId;
}
