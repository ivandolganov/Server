package com.mobapp.training.service;

import com.mobapp.training.dto.response.NotificationResponse;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.models.*;
import com.mobapp.training.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final UsersRepository usersRepository;
    private final ModuleNotificationRepository moduleNotificationRepository;
    private final CourseGroupRepository courseGroupRepository;

    @Transactional
    public List<NotificationResponse> getAllNotifications(UUID studentId) {
        // 1. Получаем группы студента
        Set<StudentGroup> studentGroups = usersRepository.findWithGroupsById(studentId)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getStudentGroups();

        // 2. Получаем ID групп
        Set<UUID> groupIds = studentGroups.stream()
                .map(StudentGroup::getId)
                .collect(Collectors.toSet());

        // 3. Получаем ID курсов через таблицу связей
        Set<UUID> courseIds = courseGroupRepository.findCourseIdsByGroupIds(groupIds);

        if (courseIds.isEmpty()) {
            return Collections.emptyList(); // Нет курсов - нет уведомлений
        }

        // 4. Получаем уведомления для этих курсов
        List<ModuleNotification> notifications = moduleNotificationRepository
                .findByModuleCourseIdIn(courseIds);

        // 5. Преобразуем в DTO
        return notifications.stream()
                .map(this::convertToNotificationResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse convertToNotificationResponse(ModuleNotification notification) {
        Modules module = notification.getModule();
        Course course = module.getCourse();
        Users teacher = course.getTeacher(); // Предполагаем, что у курса есть связь с преподавателем

        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .moduleId(module.getId())
                .moduleName(module.getName())
                .courseName(course.getName())
                .teacherFullName(teacher.getFullName())
                .endDate(module.getEndDate() != null ? module.getEndDate().toString() : "Не указана")
                .build();
    }

}