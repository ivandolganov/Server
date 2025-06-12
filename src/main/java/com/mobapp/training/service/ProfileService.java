package com.mobapp.training.service;

import com.mobapp.training.controllers.ProfileController;
import com.mobapp.training.dto.request.ProfileRequest;
import com.mobapp.training.dto.response.ProfileResponse;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.logging.ProfileLogMessages;
import com.mobapp.training.models.Users;
import com.mobapp.training.repo.UsersRepository;
import com.mobapp.training.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.*;

@Service
public class ProfileService {

    private final UsersRepository usersRepository;

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    public ProfileService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Transactional
    public ProfileResponse profile(CustomUserDetails userDetails) {
        logger.info(ProfileLogMessages.PROFILE_SERVICE_START, userDetails.getId());

        Users user = usersRepository.findById(userDetails.getId())
                .orElseThrow(() -> {
                    logger.error(ProfileLogMessages.USER_NOT_FOUND, userDetails.getId());
                    return new NotFoundException("Пользователь не найден");
                });

        ProfileResponse resp = new ProfileResponse();

        resp.setFullName(user.getFullName());
        resp.setEmail(user.getEmail());
        resp.setFirstName(user.getFirstName());
        resp.setLastName(user.getLastName());
        resp.setGender(user.getGender().name());
        resp.setPatronymic(user.getPatronymic());
        resp.setYearOfBirth(user.getYearOfBirth());

        logger.info(ProfileLogMessages.PROFILE_SERVICE_SUCCESS, userDetails.getId());

        return resp;
    }
}