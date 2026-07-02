package com.farmkart.farmer.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum FarmerStatus implements StringValuedEnum {

    PENDING_VERIFICATION("PENDING_VERIFICATION"),
    VERIFIED("VERIFIED"),
    SUSPENDED("SUSPENDED"),
    REJECTED("REJECTED");

    private final String value;

    FarmerStatus(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static FarmerStatus getFarmerStatus(String value) {
        return StringValuedEnumSupport.fromValue(FarmerStatus.class, value);
    }
}
