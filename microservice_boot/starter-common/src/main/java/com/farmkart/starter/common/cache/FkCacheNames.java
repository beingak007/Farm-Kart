package com.farmkart.starter.common.cache;

/**
 * Central registry of Redis cache names used across all microservices.
 *
 * <p>Each service's @Cacheable / @CacheEvict should reference these constants
 * to avoid typo-based cache misses and naming drift.
 */
public final class FkCacheNames {

    private FkCacheNames() {}

    // ── Farmer service ────────────────────────────────────────────────────
    public static final String FARMER_BY_ID      = "farmer:id";
    public static final String FARMER_BY_USER_ID = "farmer:userId";

    // ── Buyer service ─────────────────────────────────────────────────────
    public static final String BUYER_BY_ID      = "buyer:id";
    public static final String BUYER_BY_USER_ID = "buyer:userId";

    // ── Product Catalog ───────────────────────────────────────────────────
    public static final String PRODUCT_BY_ID       = "product:id";
    public static final String PRODUCT_CATEGORY    = "product:categories";
    public static final String PRODUCT_LIST_PAGE   = "product:list";

    // ── Market Price ──────────────────────────────────────────────────────
    public static final String MANDI_LATEST        = "mandi:latest";
    public static final String MANDI_HISTORY       = "mandi:history";

    // ── Warehouse ─────────────────────────────────────────────────────────
    public static final String WAREHOUSE_AVAILABLE = "warehouse:available";

    // ── Auth / Sessions ───────────────────────────────────────────────────
    public static final String USER_SESSION        = "session:user";
    public static final String OTP_CODE            = "otp:code";
    public static final String REFRESH_TOKEN       = "token:refresh";

    // ── Marketplace / Framework ───────────────────────────────────────────
    public static final String FRAMEWORK_MODEL     = "framework:model";
    public static final String FRAMEWORK_VIEW      = "framework:view";
}
