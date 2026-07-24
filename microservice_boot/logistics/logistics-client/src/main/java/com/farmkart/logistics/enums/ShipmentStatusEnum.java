package com.farmkart.logistics.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum ShipmentStatusEnum implements StringValuedEnum {

    CREATED("CREATED"),
    PICKED_UP("PICKED_UP"),
    IN_TRANSIT("IN_TRANSIT"),
    DELIVERED("DELIVERED"),
    FAILED("FAILED");

    private final String value;

    ShipmentStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static ShipmentStatusEnum getShipmentStatusEnum(String value) {
        return StringValuedEnumSupport.fromValue(ShipmentStatusEnum.class, value);
    }
}
