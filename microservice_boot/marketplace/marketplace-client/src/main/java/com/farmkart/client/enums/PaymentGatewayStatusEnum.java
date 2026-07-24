package com.farmkart.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum PaymentGatewayStatusEnum implements StringValuedEnum {

    SUCCESS("SUCCESS"),
    CAPTURED("CAPTURED"),
    FAILED("FAILED"),
    REFUNDED("REFUNDED");

    private final String value;

    PaymentGatewayStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static PaymentGatewayStatusEnum getPaymentGatewayStatusEnum(String value) {
        return StringValuedEnumSupport.fromValue(PaymentGatewayStatusEnum.class, value);
    }
}
