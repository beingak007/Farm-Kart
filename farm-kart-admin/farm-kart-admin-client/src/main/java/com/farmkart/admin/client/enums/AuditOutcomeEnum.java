package com.farmkart.admin.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum AuditOutcomeEnum implements StringValuedEnum {

    SUCCESS("SUCCESS"),
    FAILURE("FAILURE");

    private final String value;

    AuditOutcomeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static AuditOutcomeEnum getAuditOutcomeEnum(String value) {
        return StringValuedEnumSupport.fromValue(AuditOutcomeEnum.class, value);
    }
}
