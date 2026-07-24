package com.farmkart.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum OrderCancelReasonEnum implements StringValuedEnum {

    USER_REQUESTED("USER_REQUESTED");

    private final String value;

    OrderCancelReasonEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static OrderCancelReasonEnum getOrderCancelReasonEnum(String value) {
        return StringValuedEnumSupport.fromValue(OrderCancelReasonEnum.class, value);
    }
}
