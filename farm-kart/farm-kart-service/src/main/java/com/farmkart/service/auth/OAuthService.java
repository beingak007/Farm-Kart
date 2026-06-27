package com.farmkart.service.auth;

import com.fasterxml.jackson.databind.JsonNode;
import com.farmkart.client.dto.auth.AuthTokenResponse;
import com.farmkart.client.dto.auth.OAuthLoginRequest;
import com.farmkart.client.enums.AuthProvider;
import com.farmkart.client.enums.UserRole;
import com.farmkart.repository.UserRepository;
import com.farmkart.repository.entity.User;
import com.farmkart.starter.common.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final UserRepository userRepository;
    private final AuthService authService;
    private final RestClient restClient;
    private final String googleClientId;

    public OAuthService(
            UserRepository userRepository,
            AuthService authService,
            @Value("${farmkart.oauth.google-client-id:}") String googleClientId) {
        this.userRepository = userRepository;
        this.authService = authService;
        this.googleClientId = googleClientId;
        this.restClient = RestClient.create();
    }

    @Transactional
    public AuthTokenResponse login(OAuthLoginRequest request) {
        AuthProvider provider = parseProvider(request.provider());
        OAuthProfile profile = switch (provider) {
            case GOOGLE -> verifyGoogle(request.idToken());
            case MICROSOFT -> verifyMicrosoft(request.accessToken());
            default -> throw new BusinessException(400, "Unsupported OAuth provider");
        };

        User user = findOrCreateUser(provider, profile);
        return authService.issueTokensForUser(user);
    }

    private AuthProvider parseProvider(String provider) {
        try {
            return AuthProvider.valueOf(provider.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(400, "Invalid OAuth provider");
        }
    }

    private OAuthProfile verifyGoogle(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new BusinessException(400, "Google ID token is required");
        }

        JsonNode payload = restClient
                .get()
                .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", idToken)
                .retrieve()
                .body(JsonNode.class);

        if (payload == null || payload.has("error")) {
            throw new BusinessException(401, "Invalid Google token");
        }

        if (googleClientId != null && !googleClientId.isBlank()) {
            String audience = payload.path("aud").asText("");
            if (!googleClientId.equals(audience)) {
                throw new BusinessException(401, "Google token audience mismatch");
            }
        }

        String subject = payload.path("sub").asText(null);
        String email = payload.path("email").asText(null);
        String name = payload.path("name").asText("Google User");

        if (subject == null || email == null) {
            throw new BusinessException(401, "Google token missing required claims");
        }

        return new OAuthProfile(subject, email, name);
    }

    private OAuthProfile verifyMicrosoft(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException(400, "Microsoft access token is required");
        }

        JsonNode profile = restClient
                .get()
                .uri("https://graph.microsoft.com/v1.0/me")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(JsonNode.class);

        if (profile == null || profile.has("error")) {
            throw new BusinessException(401, "Invalid Microsoft token");
        }

        String subject = profile.path("id").asText(null);
        String email = profile.path("mail").asText(null);
        if (email == null || email.isBlank()) {
            email = profile.path("userPrincipalName").asText(null);
        }
        String name = profile.path("displayName").asText("Microsoft User");

        if (subject == null || email == null) {
            throw new BusinessException(401, "Microsoft profile missing required fields");
        }

        return new OAuthProfile(subject, email, name);
    }

    private User findOrCreateUser(AuthProvider provider, OAuthProfile profile) {
        return userRepository
                .findByAuthProviderAndProviderUserId(provider, profile.subject())
                .orElseGet(() -> linkOrCreateUser(provider, profile));
    }

    private User linkOrCreateUser(AuthProvider provider, OAuthProfile profile) {
        return userRepository
                .findByEmail(profile.email().toLowerCase())
                .map(existing -> linkExistingUser(existing, provider, profile))
                .orElseGet(() -> createOAuthUser(provider, profile));
    }

    private User linkExistingUser(User user, AuthProvider provider, OAuthProfile profile) {
        user.setAuthProvider(provider);
        user.setProviderUserId(profile.subject());
        user.setEmailVerified(true);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(profile.name());
        }
        log.info("Linked {} account for user {}", provider, user.getId());
        return userRepository.save(user);
    }

    private User createOAuthUser(AuthProvider provider, OAuthProfile profile) {
        User user = new User();
        user.setAuthProvider(provider);
        user.setProviderUserId(profile.subject());
        user.setName(profile.name());
        user.setEmail(profile.email().toLowerCase());
        user.setRole(UserRole.VENDOR);
        user.setEmailVerified(true);
        user.setMobileVerified(false);
        log.info("Created {} user for {}", provider, profile.email());
        return userRepository.save(user);
    }

    private record OAuthProfile(String subject, String email, String name) {}
}
