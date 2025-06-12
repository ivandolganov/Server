package com.mobapp.training.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.access.AccessDeniedException;

import java.io.IOException;

public class AuthorizationExceptionTranslationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            chain.doFilter(request, response);
        } catch (AuthorizationDeniedException ex) {
            throw new AccessDeniedException(ex.getMessage(), ex);
        }
    }
}
