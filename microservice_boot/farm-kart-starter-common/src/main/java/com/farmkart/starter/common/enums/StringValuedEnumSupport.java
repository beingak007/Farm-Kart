package com.farmkart.starter.common.enums;

/**
 * Shared lookup helpers for {@link StringValuedEnum} implementations.
 */
public final class StringValuedEnumSupport {

    private StringValuedEnumSupport() {}

    public static <E extends Enum<E> & StringValuedEnum> E fromValue(Class<E> type, String value) {
        if (value == null || type == null) {
            return null;
        }
        for (E constant : type.getEnumConstants()) {
            if (constant.getValue().equals(value)) {
                return constant;
            }
        }
        return null;
    }

    public static <E extends Enum<E> & StringValuedEnum> E fromValueOrThrow(Class<E> type, String value) {
        E resolved = fromValue(type, value);
        if (resolved == null) {
            throw new IllegalArgumentException("Unknown " + type.getSimpleName() + " value: " + value);
        }
        return resolved;
    }
}
