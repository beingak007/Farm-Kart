package com.farmkart.notification.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum NotificationTemplateCodeEnum implements StringValuedEnum {

    OTP_LOGIN("OTP_LOGIN"),
    WELCOME("WELCOME"),
    FARMER_WELCOME("FARMER_WELCOME"),
    FARMER_VERIFIED("FARMER_VERIFIED"),
    ORDER_CONFIRMED("ORDER_CONFIRMED"),
    PAYMENT_RECEIPT("PAYMENT_RECEIPT"),
    ORDER_DELIVERED("ORDER_DELIVERED"),
    WAREHOUSE_BOOKED("WAREHOUSE_BOOKED"),
    WAREHOUSE_LIVE_LOCATION("WAREHOUSE_LIVE_LOCATION");

    private final String value;

    NotificationTemplateCodeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static NotificationTemplateCodeEnum getNotificationTemplateCodeEnum(String value) {
        return StringValuedEnumSupport.fromValue(NotificationTemplateCodeEnum.class, value);
    }
}
