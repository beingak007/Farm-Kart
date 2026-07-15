package com.farmkart.notification.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum NotificationChannel implements StringValuedEnum {

    SMS("SMS"),
    EMAIL("EMAIL"),
    PUSH("PUSH"),
    WHATSAPP("WHATSAPP");

    private final String value;

    NotificationChannel(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static NotificationChannel getNotificationChannel(String value) {
        return StringValuedEnumSupport.fromValue(NotificationChannel.class, value);
    }
}
