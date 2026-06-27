package com.farmkart.service.auth;

import com.farmkart.client.dto.auth.*;
import com.farmkart.repository.entity.RefreshToken;
import com.farmkart.repository.entity.User;
import com.farmkart.repository.RefreshTokenRepository;
import com.farmkart.repository.UserRepository;
import com.farmkart.service.security.JwtUtil;
import com.farmkart.client.enums.UserRole;
import com.farmkart.client.enums.AuthProvider;
import com.farmkart.starter.common.exception.BusinessException;
import com.farmkart.starter.common.sms.SmsGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final String RESET_OTP_PREFIX = "reset:";

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;
    private final SmsGateway smsGateway;

    public AuthService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil,
            OtpService otpService,
            SmsGateway smsGateway) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.otpService = otpService;
        this.smsGateway = smsGateway;
    }

    @Transactional
    public AuthTokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already registered");
        }
        if (userRepository.existsByMobile(request.mobile())) {
            throw new BusinessException("Mobile already registered");
        }

        User user = new User();
        user.setName(request.name());
        user.setEmail(request.email().toLowerCase());
        user.setMobile(request.mobile());
        user.setRole(request.role() != null ? request.role() : UserRole.VENDOR);
        user.setAuthProvider(AuthProvider.LOCAL);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }
        user.setEmailVerified(false);
        user.setMobileVerified(false);
        userRepository.save(user);

        String otp = otpService.generateAndStore(user.getMobile());
        dispatchOtp(user.getMobile(), otp);

        return new AuthTokenResponse(null, null, user.getId(), user.getRole(), true);
    }

    public OtpSentResponse sendOtp(SendOtpRequest request) {
        String otp = otpService.generateAndStore(request.mobile());
        dispatchOtp(request.mobile(), otp);
        return new OtpSentResponse(true);
    }

    private void dispatchOtp(String mobile, String otp) {
        smsGateway.sendOtp(mobile, otp);
    }

    @Transactional
    public AuthTokenResponse verifyOtp(VerifyOtpRequest request) {
        if (!otpService.verify(request.mobile(), request.otp())) {
            throw new BusinessException("Invalid or expired OTP");
        }

        User user = userRepository.findByMobile(request.mobile())
                .orElseGet(() -> createMobileUser(request.mobile()));

        user.setMobileVerified(true);
        userRepository.save(user);
        return issueTokens(user);
    }

    private User createMobileUser(String mobile) {
        User user = new User();
        user.setAuthProvider(AuthProvider.MOBILE);
        user.setName("Farmer " + mobile.substring(Math.max(0, mobile.length() - 4)));
        user.setEmail(mobile + "@mobile.farmkart.local");
        user.setMobile(mobile);
        user.setRole(UserRole.VENDOR);
        user.setEmailVerified(false);
        user.setMobileVerified(true);
        return userRepository.save(user);
    }

    @Transactional
    public AuthTokenResponse login(LoginRequest request) {
        User user = findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(401, "Invalid username or password"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(401, "Invalid username or password");
        }

        return issueTokens(user);
    }

    public ForgotPasswordResponse forgotPassword(ForgotPasswordRequest request) {
        findByUsername(request.username()).ifPresent(user -> {
            String otp = otpService.generateAndStore(resetOtpKey(user));
            if (user.getMobile() != null && !user.getMobile().isBlank()) {
                smsGateway.sendOtp(user.getMobile(), otp);
            } else {
                log.info("Password reset OTP for {}: {} (no mobile on file)", request.username(), otp);
            }
        });
        return new ForgotPasswordResponse(
                true, "If an account exists, a reset code has been sent to your registered mobile.");
    }

    @Transactional
    public MessageResponse resetPassword(ResetPasswordRequest request) {
        User user = findByUsername(request.username())
                .orElseThrow(() -> new BusinessException("Invalid reset request"));

        if (!otpService.verify(resetOtpKey(user), request.otp())) {
            throw new BusinessException("Invalid or expired reset code");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
        return new MessageResponse("Password updated successfully. You can sign in now.");
    }

    private Optional<User> findByUsername(String username) {
        if (username == null || username.isBlank()) {
            return Optional.empty();
        }
        String trimmed = username.trim();
        Optional<User> byEmail = userRepository.findByEmail(trimmed.toLowerCase());
        if (byEmail.isPresent()) {
            return byEmail;
        }
        return userRepository.findByMobile(trimmed);
    }

    private String resetOtpKey(User user) {
        String key = user.getMobile() != null ? user.getMobile() : user.getEmail();
        return RESET_OTP_PREFIX + key;
    }

    public AuthTokenResponse issueTokensForUser(User user) {
        return issueTokens(user);
    }

    private AuthTokenResponse issueTokens(User user) {
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getRole().name());
        String refreshTokenValue = jwtUtil.generateRefreshTokenValue();

        refreshTokenRepository.deleteByUserId(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUserId(user.getId());
        refreshToken.setTokenHash(hashToken(refreshTokenValue));
        refreshToken.setExpiresAt(jwtUtil.refreshTokenExpiry());
        refreshTokenRepository.save(refreshToken);

        return new AuthTokenResponse(accessToken, refreshTokenValue, user.getId(), user.getRole(), false);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
