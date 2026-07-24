package com.farmkart.common.rest.security;

import com.farmkart.starter.common.enums.UserRoleEnum;
import com.farmkart.starter.common.security.FkSecurityPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Access current authenticated user from Spring Security context.
 */
public final class FkSecurityContext {

    private FkSecurityContext() {}

    public static Long currentUserId() {
        Object principal = principal();
        if (principal instanceof FkSecurityPrincipal p) {
            return p.userId();
        }
        if (principal == null) {
            return null;
        }
        try {
            return Long.parseLong(principal.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static UserRoleEnum currentRole() {
        Object principal = principal();
        if (principal instanceof FkSecurityPrincipal p) {
            return p.role();
        }
        return null;
    }

    public static boolean isMasterAdmin() {
        UserRoleEnum role = currentRole();
        return role != null && role.isMasterAdmin();
    }

    private static Object principal() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getPrincipal() : null;
    }
}
