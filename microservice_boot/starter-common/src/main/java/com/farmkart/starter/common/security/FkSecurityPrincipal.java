package com.farmkart.starter.common.security;

import com.farmkart.starter.common.enums.UserRoleEnum;

/** Minimal principal contract — implemented by {@code FkUserPrincipal} in common-rest. */
public interface FkSecurityPrincipal {
    Long userId();
    UserRoleEnum role();
}
