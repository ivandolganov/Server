package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.CreateModuleRequest;
import com.mobapp.training.dto.request.UpdateModuleRequest;
import com.mobapp.training.dto.response.CourseWithModulesResponse;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.ModuleResponse;
import com.mobapp.training.logging.ModuleControllerLogMessages;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.ModulesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Tag(name = "Modules API", description = "Управление модулями пользователей")
public class ModulesController {

    private static final Logger logger = LoggerFactory.getLogger(CoursesController.class);
    private final ModulesService modulesService;

    public ModulesController(ModulesService modulesService) {
        this.modulesService = modulesService;
    }

    @GetMapping("/{courseId}/modules")
    @Operation(
            summary = "Получить модули курса",
            description = "Модули курса"
    )
    public ResponseEntity<CourseWithModulesResponse> getModulesForStudent(
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ModuleControllerLogMessages.GET_MODULES_REQUEST, courseId, userDetails.getId());
        return ResponseEntity.ok(modulesService.getModulesForStudent(userDetails, courseId));
    }

    @PostMapping("/{courseId}/module-create")
    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(#courseId, authentication.principal.id)")
    public ResponseEntity<Map<String, UUID>> createModule(
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @RequestBody CreateModuleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ModuleControllerLogMessages.CREATE_MODULE_REQUEST, courseId, userDetails.getId());
        UUID teacherId = userDetails.getId();
        UUID moduleId = modulesService.createModule(teacherId, courseId, request);
        return ResponseEntity.ok(Map.of("moduleId", moduleId));
    }

    @GetMapping("/{courseId}/modules/{moduleId}")
    public ResponseEntity<ModuleResponse> getModuleForStudent(
            @Parameter(description = "ID модуля", required = true)
            @PathVariable UUID moduleId,
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ModuleControllerLogMessages.GET_MODULE_REQUEST,
                moduleId, courseId, userDetails.getId());
        UUID teacherId = userDetails.getId();
        return ResponseEntity.ok(modulesService.getModule(teacherId, moduleId, courseId));
    }

    @DeleteMapping("/{courseId}/modules/{moduleId}")
    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(#courseId, authentication.principal.id)")
    @Operation(summary = "Удалить модуль")
    public ResponseEntity<MessageResponse> deleteModule(
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "ID модуля", required = true)
            @PathVariable UUID moduleId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ModuleControllerLogMessages.DELETE_MODULE_REQUEST,
                moduleId, courseId, userDetails.getId());
        return ResponseEntity.ok(modulesService.deleteModule(moduleId, userDetails.getId()));
    }

    @PutMapping("/{courseId}/modules/{moduleId}/edit-module")
    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(#courseId, authentication.principal.id)")
    @Operation(summary = "Обновить модуль")
    public ResponseEntity<MessageResponse> updateModule(
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @Parameter(description = "ID модуля", required = true)
            @PathVariable UUID moduleId,
            @RequestBody UpdateModuleRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(ModuleControllerLogMessages.UPDATE_MODULE_REQUEST,
                moduleId, courseId, userDetails.getId());
        return ResponseEntity.ok(modulesService.updateModule(moduleId, userDetails.getUser(), request));
    }
}
