package com.farmkart.service.security;

import com.farmkart.client.enums.UserRole;
import com.farmkart.starter.common.enums.UserRoleEnum;
import com.farmkart.starter.common.security.FkJwtClaims;
import com.farmkart.starter.common.security.FkJwtTokenService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Component
public class JwtUtil {

    private final FkJwtTokenService jwtTokenService;
    private final long refreshTokenExpiryDays;

    public JwtUtil(FkJwtTokenService jwtTokenService,
                   @Value("${farmkart.jwt.refresh-token-expiry-days:7}") long refreshTokenExpiryDays) {
        this.jwtTokenService = jwtTokenService;
        this.refreshTokenExpiryDays = refreshTokenExpiryDays;
    }

    public String generateAccessToken(Long userId, String role) {
        UserRoleEnum fkRole = resolveRole(role);
        return jwtTokenService.generateAccessToken(userId, fkRole);
    }

    private UserRoleEnum resolveRole(String role) {
        UserRoleEnum fkRole = UserRoleEnum.resolve(role);
        if (fkRole != null) {
            return fkRole;
        }
        if (role != null) {
            try {
                return UserRole.valueOf(role).toFkRole();
            } catch (IllegalArgumentException ignored) {
                // fall through
            }
        }
        return UserRoleEnum.CUSTOMER;
    }

    public String generateRefreshTokenValue() {
        return UUID.randomUUID().toString();
    }

    public Instant refreshTokenExpiry() {
        return Instant.now().plus(refreshTokenExpiryDays, ChronoUnit.DAYS);
    }

    public Claims parseToken(String token) {
        FkJwtClaims claims = jwtTokenService.parse(token);
        return io.jsonwebtoken.Jwts.claims()
                .subject(String.valueOf(claims.userId()))
                .add("role", claims.role().name())
                .build();
    }
}
