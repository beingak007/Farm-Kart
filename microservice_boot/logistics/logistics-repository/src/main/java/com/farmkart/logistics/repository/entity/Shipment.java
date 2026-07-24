package com.farmkart.logistics.repository.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "logistics_partner_id")
    private Long logisticsPartnerId;

    @Column(name = "tracking_number", unique = true)
    private String trackingNumber;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "status", nullable = false)
    private String status = "CREATED";   // CREATED | PICKED_UP | IN_TRANSIT | DELIVERED | FAILED

    @Column(name = "expected_delivery")
    private Instant expectedDelivery;

    @Column(name = "actual_delivery")
    private Instant actualDelivery;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Column(name = "notes")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public Long getId() { return id; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getLogisticsPartnerId() { return logisticsPartnerId; }
    public void setLogisticsPartnerId(Long logisticsPartnerId) { this.logisticsPartnerId = logisticsPartnerId; }
    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public String getPickupAddress() { return pickupAddress; }
    public void setPickupAddress(String pickupAddress) { this.pickupAddress = pickupAddress; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getExpectedDelivery() { return expectedDelivery; }
    public void setExpectedDelivery(Instant expectedDelivery) { this.expectedDelivery = expectedDelivery; }
    public Instant getActualDelivery() { return actualDelivery; }
    public void setActualDelivery(Instant actualDelivery) { this.actualDelivery = actualDelivery; }
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
}
