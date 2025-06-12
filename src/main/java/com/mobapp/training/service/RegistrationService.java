package com.mobapp.training.service;

import com.mobapp.training.dto.request.EmailCheckRequest;
import com.mobapp.training.dto.request.GroupCheckRequest;
import com.mobapp.training.dto.request.RegistrationRequest;
import com.mobapp.training.dto.response.EmailCheckResponse;
import com.mobapp.training.dto.response.LoginResponse;
import com.mobapp.training.dto.response.RegistrationResponse;
import com.mobapp.training.enums.EnumUtils;
import com.mobapp.training.enums.Gender;
import com.mobapp.training.enums.UserRole;
import com.mobapp.training.logging.RegistrationLogMessages;
import com.mobapp.training.models.StudentGroup;
import com.mobapp.training.models.Users;
import com.mobapp.training.repo.DomainsRepository;
import com.mobapp.training.repo.GroupRepository;
import com.mobapp.training.repo.UsersRepository;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    private final UsersRepository usersRepository;
    private final GroupRepository groupRepository;
    private final DomainsRepository domainRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public RegistrationService(
            UsersRepository usersRepository,
            GroupRepository groupRepository,
            DomainsRepository domainRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.usersRepository = usersRepository;
        this.groupRepository = groupRepository;
        this.domainRepository = domainRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public EmailCheckResponse checkEmail(EmailCheckRequest request) {
        logger.info(RegistrationLogMessages.EMAIL_CHECK_START, request.getEmail());
        boolean exists = usersRepository.existsByEmail(request.email);
        if (exists) {
            logger.info(RegistrationLogMessages.EMAIL_EXISTS, request.email);
            return new EmailCheckResponse(true, null);
        }
        String domain = request.email.substring(request.email.indexOf('@') + 1).toLowerCase();
        logger.debug(RegistrationLogMessages.EMAIL_DOMAIN_CHECK, domain);
        boolean isTeacher = domainRepository.existsByDomain(domain);
        if (isTeacher) {
            logger.info(RegistrationLogMessages.TEACHER_DOMAIN_FOUND, domain);
        }
        return new EmailCheckResponse(false, isTeacher ? UserRole.ROLE_TEACHER.name().toLowerCase() : null);
    }

    public boolean checkGroupExists(GroupCheckRequest request) {
        logger.info(RegistrationLogMessages.GROUP_CHECK_START, request.groupCode);
        return groupRepository.existsByCode(request.groupCode);
    }

    public RegistrationResponse registerUser(RegistrationRequest request) {
        logger.info(RegistrationLogMessages.REGISTRATION_START, request.getEmail());

        Users user = new Users();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPatronymic(request.getPatronymic());
        user.setYearOfBirth(request.getBirthYear());

        Gender gender = EnumUtils.safeValueOf(Gender.class, request.getGender(), "gender");
        UserRole role = EnumUtils.safeValueOf(UserRole.class, request.getRole(), "role");
        user.setGender(gender);
        user.setRole(role);

        if (UserRole.ROLE_STUDENT.name().equalsIgnoreCase(request.getRole())) {
            Optional<StudentGroup> groupOpt = groupRepository.findByCode(request.getGroupCode());
            groupOpt.ifPresent(group -> user.getStudentGroups().add(group));
        }

        // Сохранение пользователя
        Users savedUser = usersRepository.save(user);
        logger.info(RegistrationLogMessages.USER_CREATED, savedUser.getId(), savedUser.getEmail());

        // Создание UserDetails для генерации токена
        CustomUserDetails userDetails = new CustomUserDetails(savedUser);

        // Генерация токена с использованием вашего JwtTokenProvider
        String token = jwtUtil.generateToken(userDetails);
        logger.debug(RegistrationLogMessages.TOKEN_GENERATED, savedUser.getId());

        // Возврат ответа
        return new RegistrationResponse(
                token
        );
    }
}
