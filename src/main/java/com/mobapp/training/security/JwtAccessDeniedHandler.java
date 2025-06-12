//package com.mobapp.training.security;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.mobapp.training.dto.response.ErrorResponse;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.AccessDeniedException;
//import org.springframework.security.web.access.AccessDeniedHandler;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//
//@Component
//public class JwtAccessDeniedHandler implements AccessDeniedHandler {
//
//    private final ObjectMapper objectMapper;
//
//    public JwtAccessDeniedHandler(ObjectMapper objectMapper) {
//        this.objectMapper = objectMapper;
//    }
//
//    @Override
//    public void handle(HttpServletRequest request,
//                       HttpServletResponse response,
//                       AccessDeniedException accessDeniedException) throws IOException {
//
//        ErrorResponse error = new ErrorResponse("Доступ запрещён: недостаточно прав", HttpStatus.FORBIDDEN.value());
//
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType("application/json");
//        response.getWriter().write(objectMapper.writeValueAsString(error));
//    }
//}
//
