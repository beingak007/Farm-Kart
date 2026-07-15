package com.farmkart.starter.common.constants;

import com.farmkart.starter.common.enums.FkCurrencyEnum;

/**
 * Currency defaults for the Farm Kart platform.
 */
public final class FkCurrencyConstants {

    public static final FkCurrencyEnum DEFAULT = FkCurrencyEnum.INR;
    public static final String DEFAULT_CODE = DEFAULT.getValue();

    private FkCurrencyConstants() {}
}
