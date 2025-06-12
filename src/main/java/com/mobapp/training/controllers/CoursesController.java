package com.mobapp.training.controllers;

import com.mobapp.training.dto.request.CourseIdRequest;
import com.mobapp.training.dto.request.CreateCourseRequest;
import com.mobapp.training.dto.request.EditCourseRequest;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.enums.UserRole;
import com.mobapp.training.exception.AccessDeniedException;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.CourseLogMessages;
import com.mobapp.training.models.Users;
import com.mobapp.training.repo.UsersRepository;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.service.CoursesService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Tag(name = "Courses API", description = "Управление курсами пользователей")
@SecurityRequirement(name = "JWT")
public class CoursesController {

    private final CoursesService coursesService;
    private final UsersRepository usersRepository;
    private static final Logger logger = LoggerFactory.getLogger(CoursesController.class);

    @Transactional
    @GetMapping
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    @Operation(
            summary = "Получить курсы пользователя",
            description = "Возвращает курсы в зависимости от роли пользователя (STUDENT или TEACHER)"
    )
    public ResponseEntity<?> getUserCourses(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(CourseLogMessages.GET_USER_COURSES, userDetails.getId(), userDetails.getRole());

        Users user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(CourseLogMessages.USER_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });

        if (user.getRole() == UserRole.ROLE_STUDENT) {
            logger.debug("Получение курсов для студента: {}", user.getId());
            return ResponseEntity.ok(coursesService.getCoursesForStudent(user));
        } else if (user.getRole() == UserRole.ROLE_TEACHER) {
            logger.debug("Получение курсов для преподавателя: {}", user.getId());
            return ResponseEntity.ok(coursesService.getCoursesForTeacher(user));
        }

        logger.warn(CourseLogMessages.ACCESS_DENIED, user.getRole());
        throw new AccessDeniedException("Доступ запрещен для вашей роли");
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('TEACHER')")
    @Operation(
            summary = "Создать новый курс",
            description = "Доступно только для преподавателей"
    )
    public ResponseEntity<MessageResponse> createCourse(
            @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(CourseLogMessages.CREATE_COURSE_START, userDetails.getId());

        Users user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(CourseLogMessages.USER_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });

        return ResponseEntity.ok(coursesService.createCourse(user, request));
    }


    @GetMapping("/edit/{courseId}")
    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(#courseId, authentication.principal.id)")
    @Operation(
            summary = "Получить страницу редактирования курса",
            description = "Доступно только владельцу курса"
    )
    public ResponseEntity<?> getEditCoursePage(
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(CourseLogMessages.GET_EDIT_PAGE, courseId);
        return ResponseEntity.ok(coursesService.editCourse(courseId));
    }

     //Сохранение изменений курса
    @PutMapping("/edit/{courseId}/save")
    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(#courseId, authentication.principal.id)")
    @Operation(
            summary = "Сохранить курс после редактирования",
            description = "Доступно только владельцу курса"
    )
    public ResponseEntity<MessageResponse> saveCourseChanges(
            @Parameter(description = "ID курса", required = true)
            @PathVariable UUID courseId,
            @RequestBody EditCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        logger.info(CourseLogMessages.SAVE_COURSE_CHANGES, courseId);
        return ResponseEntity.ok(coursesService.saveCourseChanges(courseId, request));
    }

    @DeleteMapping("/delete-course")
    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(#request.courseId, authentication.principal.id)")
    public ResponseEntity<MessageResponse> deleteCourse(
            @RequestBody CourseIdRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {

        logger.info(CourseLogMessages.DELETE_COURSE_START, request.getCourseId());
        return ResponseEntity.ok(coursesService.deleteCourse(userDetails.getId(), request.getCourseId()));
    }
}

