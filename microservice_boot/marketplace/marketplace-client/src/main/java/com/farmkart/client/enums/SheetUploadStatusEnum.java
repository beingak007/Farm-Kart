package com.farmkart.client.enums;

import com.farmkart.starter.common.enums.StringValuedEnum;
import com.farmkart.starter.common.enums.StringValuedEnumSupport;

public enum SheetUploadStatusEnum implements StringValuedEnum {

    QUEUED("QUEUED"),
    PROCESSING("PROCESSING"),
    PROCESSED("PROCESSED"),
    FAILED("FAILED");

    private final String value;

    SheetUploadStatusEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static SheetUploadStatusEnum getSheetUploadStatusEnum(String value) {
        return StringValuedEnumSupport.fromValue(SheetUploadStatusEnum.class, value);
    }
}
