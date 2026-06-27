package com.farmkart.starter.common.events;

/**
 * Central registry of all Kafka topic names used across the platform.
 *
 * <p>Every microservice that produces or consumes messages must reference
 * these constants to guarantee topic name consistency.
 */
public final class FkTopics {

    private FkTopics() {}

    // ── User / Auth ────────────────────────────────────────────────────────
    public static final String USER_REGISTERED      = "farmkart.user.registered";
    public static final String USER_ROLE_CHANGED    = "farmkart.user.role.changed";

    // ── Farmer ────────────────────────────────────────────────────────────
    public static final String FARMER_CREATED       = "farmkart.farmer.created";
    public static final String FARMER_VERIFIED      = "farmkart.farmer.verified";
    public static final String FARM_UPDATED         = "farmkart.farm.updated";

    // ── Buyer ─────────────────────────────────────────────────────────────
    public static final String BUYER_CREATED        = "farmkart.buyer.created";

    // ── Product Catalog ───────────────────────────────────────────────────
    public static final String CROP_LISTED          = "farmkart.crop.listed";
    public static final String CROP_UPDATED         = "farmkart.crop.updated";
    public static final String CROP_DELISTED        = "farmkart.crop.delisted";

    // ── Order ─────────────────────────────────────────────────────────────
    public static final String ORDER_CREATED        = "farmkart.order.created";
    public static final String ORDER_CONFIRMED      = "farmkart.order.confirmed";
    public static final String ORDER_CANCELLED      = "farmkart.order.cancelled";
    public static final String ORDER_DELIVERED      = "farmkart.order.delivered";

    // ── Payment ───────────────────────────────────────────────────────────
    public static final String PAYMENT_INITIATED    = "farmkart.payment.initiated";
    public static final String PAYMENT_SUCCESS      = "farmkart.payment.success";
    public static final String PAYMENT_FAILED       = "farmkart.payment.failed";
    public static final String SETTLEMENT_COMPLETED = "farmkart.payment.settlement.completed";

    // ── Logistics ─────────────────────────────────────────────────────────
    public static final String SHIPMENT_CREATED     = "farmkart.shipment.created";
    public static final String SHIPMENT_PICKED_UP   = "farmkart.shipment.picked_up";
    public static final String SHIPMENT_DELIVERED   = "farmkart.shipment.delivered";

    // ── Warehouse ─────────────────────────────────────────────────────────
    public static final String WAREHOUSE_BOOKED     = "farmkart.warehouse.booked";
    public static final String WAREHOUSE_RELEASED   = "farmkart.warehouse.released";

    // ── Market Price ──────────────────────────────────────────────────────
    public static final String PRICE_UPDATED        = "farmkart.market.price.updated";

    // ── Sheet Upload ──────────────────────────────────────────────────────
    public static final String SHEET_UPLOADED       = "farmkart.sheet.uploaded";
    public static final String MEDIA_UPLOADED       = "farmkart.media.uploaded";

    // ── Insurance ─────────────────────────────────────────────────────────
    public static final String INSURANCE_REQUESTED  = "farmkart.insurance.requested";
    public static final String LOAN_REQUESTED       = "farmkart.loan.requested";

    // ── Notification ──────────────────────────────────────────────────────
    public static final String NOTIFICATION_TRIGGERED = "farmkart.notification.triggered";

    // ── Admin / Audit ─────────────────────────────────────────────────────
    public static final String AUDIT_LOG_CREATED    = "farmkart.audit.log.created";

    // ── AI Agent ──────────────────────────────────────────────────────────
    public static final String AGENT_TASK_CREATED   = "farmkart.agent.task.created";
    public static final String AGENT_TASK_COMPLETED = "farmkart.agent.task.completed";
}
