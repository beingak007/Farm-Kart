package com.farmkart.warehouse.repository.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "warehouses")
public class Warehouse {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "state", nullable = false)
    private String state;

    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "pincode", nullable = false)
    private String pincode;

    @Column(name = "total_capacity_tons", nullable = false)
    private Double totalCapacityTons;

    @Column(name = "available_capacity_tons", nullable = false)
    private Double availableCapacityTons;

    @Column(name = "is_cold_storage")
    private boolean coldStorage;

    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Column(name = "owner_id")
    private Long ownerId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public Double getTotalCapacityTons() { return totalCapacityTons; }
    public void setTotalCapacityTons(Double totalCapacityTons) { this.totalCapacityTons = totalCapacityTons; }
    public Double getAvailableCapacityTons() { return availableCapacityTons; }
    public void setAvailableCapacityTons(Double availableCapacityTons) { this.availableCapacityTons = availableCapacityTons; }
    public boolean isColdStorage() { return coldStorage; }
    public void setColdStorage(boolean coldStorage) { this.coldStorage = coldStorage; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public Instant getCreatedAt() { return createdAt; }
}
