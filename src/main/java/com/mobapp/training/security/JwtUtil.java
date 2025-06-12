package com.mobapp.training.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey jwtSecretKey;
    private final int jwtExpirationInMs;

    public JwtUtil(
            @Value("${jwt.secret}") String base64Secret,
            @Value("${jwt.expiration}") int jwtExpirationInMs
    ) {
        // Декодируем Base64 и создаем безопасный ключ
        this.jwtSecretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret));
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    // Остальные методы остаются без изменений
    public String generateToken(CustomUserDetails userDetails) {
        // Используем jwtSecretKey вместо jwtSecret
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("role", userDetails.getRole())
                .claim("id", userDetails.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(jwtSecretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtSecretKey).build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }
}