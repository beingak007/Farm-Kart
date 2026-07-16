package com.farmkart.client.constants;

import com.farmkart.starter.common.enums.FkServiceNameEnum;

public final class MarketplaceServiceConstants {

    public static final FkServiceNameEnum SERVICE_NAME = FkServiceNameEnum.MARKETPLACE;
    public static final String SERVICE_ORIGIN = SERVICE_NAME.getValue();
    public static final String KAFKA_CONSUMER_GROUP = "farmkart-backend";

    private MarketplaceServiceConstants() {}
}
