package com.mobapp.training.service;

import com.mobapp.training.dto.request.AddGroupRequest;
import com.mobapp.training.dto.request.PasswordChangeRequest;
import com.mobapp.training.dto.request.SettingsRequest;
import com.mobapp.training.dto.response.MessageResponse;
import com.mobapp.training.dto.response.SettingsResponse;
import com.mobapp.training.enums.EnumUtils;
import com.mobapp.training.enums.Gender;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.SettingsLogMessages;
import com.mobapp.training.models.StudentGroup;
import com.mobapp.training.models.Users;
import com.mobapp.training.repo.GroupRepository;
import com.mobapp.training.repo.UsersRepository;
import com.mobapp.training.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);
    private final UsersRepository usersRepository;
    private final GroupRepository groupRepository;
    private final PasswordEncoder passwordEncoder;

    public SettingsService(
            UsersRepository usersRepository,
            GroupRepository groupRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usersRepository = usersRepository;
        this.groupRepository = groupRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MessageResponse addGroup(AddGroupRequest request, UUID userId) {
        logger.info(SettingsLogMessages.ADD_GROUP_START,
                userId, request.getGroupCode());

        Users student = usersRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error(SettingsLogMessages.STUDENT_NOT_FOUND, userId);
                    return new NotFoundException("Пользователь не найден");
                });

        StudentGroup group = groupRepository.findByCode(request.getGroupCode())
                .orElseThrow(() -> {
                    logger.error(SettingsLogMessages.GROUP_NOT_FOUND, request.getGroupCode());
                    return new NotFoundException("Группа с таким кодом не найдена");
                });

        student.getStudentGroups().add(group);

        logger.info(SettingsLogMessages.GROUP_ADDED,
                userId, request.getGroupCode());

        return new MessageResponse("Группа успешно добавлена");
    }

    public MessageResponse passwordChange(PasswordChangeRequest request, CustomUserDetails userDetails) {
        logger.info(SettingsLogMessages.PASSWORD_CHANGE_START, userDetails.getId());

        Users user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(SettingsLogMessages.STUDENT_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usersRepository.save(user);

        logger.info(SettingsLogMessages.PASSWORD_CHANGED, userDetails.getId());

        return new MessageResponse("Пароль успешно изменен");
    }

    public SettingsResponse settings(CustomUserDetails userDetails) {
        logger.info(SettingsLogMessages.GET_SETTINGS_START, userDetails.getId());

        Users user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(SettingsLogMessages.STUDENT_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });

        logger.info(SettingsLogMessages.SETTINGS_RETRIEVED, userDetails.getId());

        return new SettingsResponse(user.getFirstName(), user.getLastName(),
                user.getPatronymic(), user.getEmail(), user.getYearOfBirth(), user.getGender().name());
    }

    public MessageResponse settingsSave(SettingsRequest request, CustomUserDetails userDetails) {
        logger.info(SettingsLogMessages.SAVE_SETTINGS_START, userDetails.getId());

        Users user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(SettingsLogMessages.STUDENT_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPatronymic(request.getPatronymic());
        user.setYearOfBirth(request.getYearOfBirth());
        Gender gender = EnumUtils.safeValueOf(Gender.class, request.getGender(), "gender");
        user.setGender(gender);
        logger.debug(SettingsLogMessages.GENDER_UPDATED, userDetails.getId(), gender);

        usersRepository.save(user);
        logger.info(SettingsLogMessages.SETTINGS_SAVED, userDetails.getId());

        return new MessageResponse("Данные успешно изменены!");
    }
}
