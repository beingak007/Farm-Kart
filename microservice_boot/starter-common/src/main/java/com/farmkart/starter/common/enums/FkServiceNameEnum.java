package com.farmkart.starter.common.enums;

/**
 * Canonical Kafka / audit service-origin identifiers for every microservice.
 */
public enum FkServiceNameEnum implements StringValuedEnum {

    MARKETPLACE("marketplace-service"),
    FARMER("farmer-service"),
    BUYER("buyer-service"),
    LOGISTICS("logistics-service"),
    WAREHOUSE("warehouse-service"),
    MARKET_PRICE("market-price-service"),
    ADMIN("admin-service"),
    NOTIFICATION("notification-service"),
    PRODUCT_CATALOG("product-catalog-service"),
    REPORTING("reporting-service"),
    AI_ADVISORY("ai-advisory-service"),
    AGENT("agent-service");

    private final String value;

    FkServiceNameEnum(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static FkServiceNameEnum getFkServiceNameEnum(String value) {
        return StringValuedEnumSupport.fromValue(FkServiceNameEnum.class, value);
    }
}
