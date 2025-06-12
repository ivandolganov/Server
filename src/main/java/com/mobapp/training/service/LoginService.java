package com.mobapp.training.service;

import com.mobapp.training.dto.request.LoginRequest;
import com.mobapp.training.dto.response.LoginResponse;
import com.mobapp.training.exception.InvalidPasswordException;
import com.mobapp.training.exception.NotFoundException;
import com.mobapp.training.models.Users;
import com.mobapp.training.repo.UsersRepository;
import com.mobapp.training.security.CustomUserDetails;
import com.mobapp.training.security.JwtUtil;
import com.mobapp.training.logging.LoginLogMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginService.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public LoginService(UsersRepository usersRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtTokenProvider,
                        AuthenticationManager authenticationManager) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    public Optional<LoginResponse> login(LoginRequest request) {
        logger.debug(LoginLogMessages.FINDING_USER, request.getEmail());

        Users user = usersRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    logger.warn(LoginLogMessages.USER_NOT_FOUND, request.getEmail());
                    return new NotFoundException("Неверные данные пользователя");
                });

        logger.debug(LoginLogMessages.USER_FOUND, user.getId());

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            logger.warn(LoginLogMessages.INVALID_PASSWORD, request.getEmail());
            throw new InvalidPasswordException("Неверные данные пользователя");
        }

        logger.debug(LoginLogMessages.PASSWORD_CONFIRMED);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        logger.debug(LoginLogMessages.AUTH_SUCCESS, user.getId());

        String token = jwtTokenProvider.generateToken(new CustomUserDetails(user));
        logger.info(LoginLogMessages.JWT_CREATED, user.getId());

        return Optional.of(new LoginResponse(token));
    }
}
