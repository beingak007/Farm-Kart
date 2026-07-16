package com.farmkart.starter.common.enums;

/**
 * Platform-wide user roles. {@link #MASTER_ADMIN} bypasses all API restrictions.
 */
public enum UserRoleEnum implements StringValuedEnum {

    MASTER_ADMIN("MASTER_ADMIN"),
    VENDOR("VENDOR"),
    DISTRIBUTOR("DISTRIBUTOR"),
    RESELLER("RESELLER"),
    BUYER("BUYER"),
    SELLER("SELLER"),
    CUSTOMER("CUSTOMER"),

    /** @deprecated Use {@link #MASTER_ADMIN} */
    @Deprecated
    ADMIN("ADMIN");

    private final String value;

    UserRoleEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public String authority() {
        return "ROLE_" + name();
    }

    public boolean isMasterAdmin() {
        return this == MASTER_ADMIN || this == ADMIN;
    }

    public static UserRoleEnum getFkUserRoleEnum(String value) {
        return StringValuedEnumSupport.fromValue(UserRoleEnum.class, value);
    }

    /** Resolves role from JWT/DB value; maps legacy ADMIN → MASTER_ADMIN privileges. */
    public static UserRoleEnum resolve(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        UserRoleEnum role = getFkUserRoleEnum(value.toUpperCase());
        if (role == null) {
            return null;
        }
        return role == ADMIN ? MASTER_ADMIN : role;
    }
}
