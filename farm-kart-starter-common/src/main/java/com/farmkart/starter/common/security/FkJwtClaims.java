package com.farmkart.starter.common.security;

import com.farmkart.starter.common.enums.UserRoleEnum;

public record FkJwtClaims(Long userId, UserRoleEnum role) {}
