package com.farmkart.notification.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum NotificationTemplateVarEnum implements StringValuedEnum {

    OTP("otp"),
    NAME("name"),
    FARM_NAME("farmName"),
    STATE("state"),
    ORDER_ID("orderId"),
    TOTAL("total"),
    AMOUNT("amount"),
    CURRENCY("currency"),
    TRACKING_NUMBER("trackingNumber"),
    WAREHOUSE_NAME("warehouseName"),
    WAREHOUSE_ADDRESS("warehouseAddress"),
    DISTANCE_KM("distanceKm"),
    RENT("rent"),
    QUANTITY_TONS("quantityTons"),
    START_DATE("startDate"),
    END_DATE("endDate"),
    BOOKING_ID("bookingId"),
    MAPS_URL("mapsUrl"),
    LATITUDE("latitude"),
    LONGITUDE("longitude");

    private final String value;

    NotificationTemplateVarEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static NotificationTemplateVarEnum getNotificationTemplateVarEnum(String value) {
        return StringValuedEnumSupport.fromValue(NotificationTemplateVarEnum.class, value);
    }
}
