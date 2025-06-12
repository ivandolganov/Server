package com.mobapp.training.security;

import com.mobapp.training.exception.AccessDeniedException;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.models.Course;
import com.mobapp.training.repo.CourseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class CourseSecurity {

    private final CourseRepository courseRepository;

    public boolean isCourseOwner(UUID courseId, UUID teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Курс не найден с id: " + courseId));

        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем курса");
        }

        return true;
    }
}