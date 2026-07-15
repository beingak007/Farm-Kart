package com.farmkart.buyer.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum BuyerStatusEnum implements StringValuedEnum {

    ACTIVE("ACTIVE"),
    SUSPENDED("SUSPENDED");

    private final String value;

    BuyerStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static BuyerStatusEnum getBuyerStatusEnum(String value) {
        return StringValuedEnumSupport.fromValue(BuyerStatusEnum.class, value);
    }
}
