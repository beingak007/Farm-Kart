package com.farmkart.warehouse.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum WarehouseStatusEnum implements StringValuedEnum {

    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    MAINTENANCE("MAINTENANCE");

    private final String value;

    WarehouseStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static WarehouseStatusEnum getWarehouseStatusEnum(String value) {
        return StringValuedEnumSupport.fromValue(WarehouseStatusEnum.class, value);
    }
}
