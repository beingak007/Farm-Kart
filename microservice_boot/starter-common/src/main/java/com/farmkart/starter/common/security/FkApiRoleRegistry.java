package com.farmkart.starter.common.security;

import com.farmkart.starter.common.enums.UserRoleEnum;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Maps normalized {@code /api/v1/...} paths to allowed roles.
 * {@link UserRoleEnum#MASTER_ADMIN} is handled separately (always allowed).
 */
public final class FkApiRoleRegistry {

    private record Rule(Pattern pattern, Set<UserRoleEnum> roles) {}

    private static final List<Rule> RULES = List.of(
            rule("/api/v1/vendors/", UserRoleEnum.VENDOR, UserRoleEnum.SELLER, UserRoleEnum.RESELLER),
            rule("/api/v1/buyers/", UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER),
            rule("/api/v1/farmers/", UserRoleEnum.SELLER, UserRoleEnum.VENDOR),
            rule("/api/v1/orders/", UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER, UserRoleEnum.VENDOR, UserRoleEnum.RESELLER),
            rule("/api/v1/products/", UserRoleEnum.VENDOR, UserRoleEnum.SELLER, UserRoleEnum.RESELLER,
                    UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER, UserRoleEnum.DISTRIBUTOR),
            rule("/api/v1/payments/", UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER, UserRoleEnum.VENDOR),
            rule("/api/v1/shipments/", UserRoleEnum.DISTRIBUTOR, UserRoleEnum.RESELLER, UserRoleEnum.VENDOR),
            rule("/api/v1/warehouses/", UserRoleEnum.DISTRIBUTOR, UserRoleEnum.VENDOR, UserRoleEnum.SELLER),
            rule("/api/v1/catalog/", UserRoleEnum.VENDOR, UserRoleEnum.SELLER, UserRoleEnum.RESELLER,
                    UserRoleEnum.DISTRIBUTOR, UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER),
            rule("/api/v1/market-prices/", UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER, UserRoleEnum.VENDOR,
                    UserRoleEnum.SELLER, UserRoleEnum.DISTRIBUTOR, UserRoleEnum.RESELLER),
            rule("/api/v1/notifications/", UserRoleEnum.VENDOR, UserRoleEnum.DISTRIBUTOR, UserRoleEnum.RESELLER,
                    UserRoleEnum.BUYER, UserRoleEnum.SELLER, UserRoleEnum.CUSTOMER),
            rule("/api/v1/admin/", UserRoleEnum.MASTER_ADMIN),
            rule("/api/v1/reports/", UserRoleEnum.MASTER_ADMIN, UserRoleEnum.VENDOR, UserRoleEnum.DISTRIBUTOR),
            rule("/api/v1/ai-advisory/", UserRoleEnum.SELLER, UserRoleEnum.VENDOR, UserRoleEnum.BUYER, UserRoleEnum.CUSTOMER),
            rule("/api/v1/agent/", UserRoleEnum.VENDOR, UserRoleEnum.DISTRIBUTOR, UserRoleEnum.RESELLER,
                    UserRoleEnum.BUYER, UserRoleEnum.SELLER, UserRoleEnum.CUSTOMER),
            rule("/api/v1/framework/", UserRoleEnum.MASTER_ADMIN),
            rule("/api/v1/sheets/", UserRoleEnum.VENDOR, UserRoleEnum.SELLER)
    );

    private static final Set<String> PUBLIC_PREFIXES = Set.of(
            "/api/v1/auth/",
            "/api/v1/currencies"
    );

    private FkApiRoleRegistry() {}

    public static boolean isPublicPath(String normalizedPath) {
        if (normalizedPath == null) {
            return false;
        }
        return PUBLIC_PREFIXES.stream().anyMatch(normalizedPath::startsWith);
    }

    public static Optional<Set<UserRoleEnum>> allowedRoles(String normalizedPath) {
        if (normalizedPath == null || isPublicPath(normalizedPath)) {
            return Optional.empty();
        }
        for (Rule rule : RULES) {
            if (rule.pattern.matcher(normalizedPath).find()) {
                return Optional.of(rule.roles);
            }
        }
        // Authenticated API with no explicit rule — any logged-in role
        if (normalizedPath.startsWith("/api/v1/")) {
            return Optional.of(EnumSet.allOf(UserRoleEnum.class));
        }
        return Optional.empty();
    }

    public static String normalizePath(String requestUri) {
        if (requestUri == null) {
            return "";
        }
        int idx = requestUri.indexOf("/api/v1");
        return idx >= 0 ? requestUri.substring(idx) : requestUri;
    }

    private static Rule rule(String prefix, UserRoleEnum... roles) {
        String regex = Pattern.quote(prefix);
        return new Rule(Pattern.compile(regex), EnumSet.copyOf(Arrays.asList(roles)));
    }
}
