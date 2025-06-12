package com.mobapp.training.exception;

import org.springframework.security.core.AuthenticationException;

public class ExpiredJwtException extends AuthenticationException {
    public ExpiredJwtException() {
        super("JWT token expired");
    }
}