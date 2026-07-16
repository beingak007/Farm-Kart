package com.farmkart.starter.common.enums;

public enum FieldValidationCodeEnum implements StringValuedEnum {

    INVALID("INVALID"),
    REQUIRED("REQUIRED");

    private final String value;

    FieldValidationCodeEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static FieldValidationCodeEnum getFieldValidationCodeEnum(String value) {
        return StringValuedEnumSupport.fromValue(FieldValidationCodeEnum.class, value);
    }
}
