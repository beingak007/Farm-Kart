package com.farmkart.client.enums;

import com.farmkart.starter.common.enums.UserRoleEnum;

/**
 * Marketplace user roles — aligned with {@link UserRoleEnum} platform registry.
 */
public enum UserRole {
    MASTER_ADMIN,
    VENDOR,
    DISTRIBUTOR,
    RESELLER,
    BUYER,
    SELLER,
    CUSTOMER,

    /** @deprecated Use {@link #MASTER_ADMIN} */
    @Deprecated
    ADMIN,
    /** @deprecated Use {@link #SELLER} */
    @Deprecated
    FARMER;

    public UserRoleEnum toFkRole() {
        return switch (this) {
            case MASTER_ADMIN, ADMIN -> UserRoleEnum.MASTER_ADMIN;
            case FARMER -> UserRoleEnum.SELLER;
            default -> UserRoleEnum.valueOf(name());
        };
    }

    public static UserRole fromFkRole(UserRoleEnum role) {
        if (role == null) {
            return CUSTOMER;
        }
        return switch (role) {
            case MASTER_ADMIN -> MASTER_ADMIN;
            case ADMIN -> MASTER_ADMIN;
            default -> UserRole.valueOf(role.name());
        };
    }
}
