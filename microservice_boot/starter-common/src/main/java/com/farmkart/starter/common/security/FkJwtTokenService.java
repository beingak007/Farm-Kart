package com.farmkart.starter.common.security;

import com.farmkart.starter.common.enums.UserRoleEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * Shared JWT validation/creation used by marketplace (issuer) and all microservices (validators).
 */
@Component
public class FkJwtTokenService {

    private final SecretKey secretKey;
    private final long accessTokenExpiryMinutes;

    public FkJwtTokenService(
            @Value("${farmkart.jwt.secret:change-me-in-production-use-at-least-256-bits-secret-key-here}") String secret,
            @Value("${farmkart.jwt.access-token-expiry-minutes:15}") long accessTokenExpiryMinutes) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiryMinutes = accessTokenExpiryMinutes;
    }

    public String generateAccessToken(Long userId, UserRoleEnum role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("role", role != null ? role.name() : UserRoleEnum.CUSTOMER.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessTokenExpiryMinutes, ChronoUnit.MINUTES)))
                .signWith(secretKey)
                .compact();
    }

    public FkJwtClaims parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Long userId = Long.parseLong(claims.getSubject());
        UserRoleEnum role = UserRoleEnum.resolve(claims.get("role", String.class));
        return new FkJwtClaims(userId, role != null ? role : UserRoleEnum.CUSTOMER);
    }
}
