package com.farmkart.rest.controller;

import com.farmkart.client.dto.auth.*;
import com.farmkart.service.auth.AuthService;
import com.farmkart.service.auth.OAuthService;
import com.farmkart.starter.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Mobile OTP login, Google OAuth, registration, and password management")
@SecurityRequirements
public class AuthController {

    private final AuthService authService;
    private final OAuthService oauthService;

    public AuthController(AuthService authService, OAuthService oauthService) {
        this.authService = authService;
        this.oauthService = oauthService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates account and sends OTP to mobile for verification.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "User registered; OTP sent to mobile"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Email or mobile already registered")
    })
    public ApiResponse<AuthTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.ok("Registration successful. Verify OTP sent to mobile.", authService.register(request));
    }

    @PostMapping("/send-otp")
    @Operation(
            summary = "Send OTP to mobile",
            description = """
                    Generates a 6-digit OTP, stores it in Redis (5-minute TTL), and sends it via SMS \
                    (Fast2SMS when configured, console log in dev). Rate limited to 3 requests per 15 minutes.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP sent successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too many OTP requests or account temporarily locked"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid mobile number")
    })
    public ApiResponse<OtpSentResponse> sendOtp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mobile number to receive OTP",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = SendOtpRequest.class),
                            examples = @ExampleObject(value = "{\"mobile\":\"9876543210\"}")))
            @Valid @RequestBody SendOtpRequest request) {
        return ApiResponse.ok("OTP sent successfully", authService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    @Operation(
            summary = "Verify OTP and login",
            description = """
                    Validates OTP against Redis. Creates a new user if mobile is not registered, \
                    then returns JWT access and refresh tokens. Max 5 failed attempts before 15-minute lock.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP verified; JWT tokens issued"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid or expired OTP"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "429", description = "Too many failed attempts; account locked")
    })
    public ApiResponse<AuthTokenResponse> verifyOtp(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Mobile number and 6-digit OTP",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = VerifyOtpRequest.class),
                            examples = @ExampleObject(value = "{\"mobile\":\"9876543210\",\"otp\":\"654321\"}")))
            @Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponse.ok(authService.verifyOtp(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username and password", description = "Username may be email or mobile number.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid username or password")
    })
    public ApiResponse<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/oauth/login")
    @Operation(
            summary = "Login with Google or Microsoft",
            description = """
                    Verifies OAuth token server-side and issues JWT. \
                    For Google pass `idToken`; for Microsoft pass `accessToken`.""")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OAuth login successful"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid or expired OAuth token")
    })
    public ApiResponse<AuthTokenResponse> oauthLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "OAuth provider and token",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = OAuthLoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Google login",
                                    value = "{\"provider\":\"GOOGLE\",\"idToken\":\"google-id-token-here\"}")))
            @Valid @RequestBody OAuthLoginRequest request) {
        return ApiResponse.ok(oauthService.login(request));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset OTP", description = "Sends OTP to registered mobile if account exists.")
    public ApiResponse<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ApiResponse.ok(authService.forgotPassword(request));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password with OTP", description = "Verifies reset OTP and sets a new password.")
    public ApiResponse<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ApiResponse.ok(authService.resetPassword(request));
    }
}
