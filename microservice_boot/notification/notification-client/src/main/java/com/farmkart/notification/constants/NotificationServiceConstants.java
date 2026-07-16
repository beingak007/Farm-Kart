package com.farmkart.notification.constants;

import com.farmkart.starter.common.enums.FkServiceNameEnum;

public final class NotificationServiceConstants {

    public static final FkServiceNameEnum SERVICE_NAME = FkServiceNameEnum.NOTIFICATION;
    public static final String SERVICE_ORIGIN = SERVICE_NAME.getValue();
    public static final String KAFKA_GROUP_DOMAIN = "notification-domain";
    public static final String KAFKA_GROUP_DISPATCH = "notification-service";

    private NotificationServiceConstants() {}
}
