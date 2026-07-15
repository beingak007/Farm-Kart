package com.farmkart.common.rest.security;

import com.farmkart.starter.common.enums.UserRoleEnum;
import com.farmkart.starter.common.security.FkApiRoleRegistry;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Master Admin bypasses all role checks; other users match path → role registry.
 */
public class FkRoleAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier,
                                         RequestAuthorizationContext context) {
        String normalized = FkApiRoleRegistry.normalizePath(context.getRequest().getRequestURI());

        if (FkApiRoleRegistry.isPublicPath(normalized)) {
            return new AuthorizationDecision(true);
        }

        Authentication auth = authenticationSupplier.get();
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        if (auth.getPrincipal() instanceof FkUserPrincipal principal && principal.isMasterAdmin()) {
            return new AuthorizationDecision(true);
        }

        Optional<Set<UserRoleEnum>> allowed = FkApiRoleRegistry.allowedRoles(normalized);
        if (allowed.isEmpty()) {
            return new AuthorizationDecision(true);
        }

        UserRoleEnum userRole = extractRole(auth);
        if (userRole == null) {
            return new AuthorizationDecision(false);
        }
        if (userRole.isMasterAdmin()) {
            return new AuthorizationDecision(true);
        }

        return new AuthorizationDecision(allowed.get().contains(userRole));
    }

    private UserRoleEnum extractRole(Authentication auth) {
        if (auth.getPrincipal() instanceof FkUserPrincipal principal) {
            return principal.getRole();
        }
        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .map(UserRoleEnum::resolve)
                .filter(r -> r != null)
                .findFirst()
                .orElse(null);
    }
}
