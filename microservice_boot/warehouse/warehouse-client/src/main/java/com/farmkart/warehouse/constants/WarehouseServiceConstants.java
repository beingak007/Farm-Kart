package com.farmkart.warehouse.constants;

import com.farmkart.starter.common.enums.FkServiceNameEnum;

public final class WarehouseServiceConstants {

    public static final FkServiceNameEnum SERVICE_NAME = FkServiceNameEnum.WAREHOUSE;
    public static final String SERVICE_ORIGIN = SERVICE_NAME.getValue();

    /** Default Uber-style match radius (km). */
    public static final double DEFAULT_MATCH_RADIUS_KM = 100.0;

    /** Cold storage rent multiplier (20% surcharge). */
    public static final java.math.BigDecimal COLD_STORAGE_RATE_MULTIPLIER = new java.math.BigDecimal("1.20");

    private WarehouseServiceConstants() {}
}
