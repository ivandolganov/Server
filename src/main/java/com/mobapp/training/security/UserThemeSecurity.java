package com.mobapp.training.security;

import com.mobapp.training.exception.AccessDeniedException;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.models.Course;
import com.mobapp.training.models.WordTheme;
import com.mobapp.training.repo.CourseRepository;
import com.mobapp.training.repo.WordThemeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class UserThemeSecurity {

    private final WordThemeRepository wordThemeRepository;

    public boolean isThemeOwner(UUID themeId, UUID userId) {
        WordTheme theme = wordThemeRepository.findById(themeId)
                .orElseThrow(() -> new NotFoundException("Набор не найден с id: " + themeId));

        if (!theme.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Вы не являетесь владельцем набора");
        }

        return true;
    }
}