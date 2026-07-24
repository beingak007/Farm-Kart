package com.farmkart.logistics.repository;

import com.farmkart.logistics.repository.entity.Shipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    List<Shipment> findByOrderId(Long orderId);
    Page<Shipment> findByStatus(String status, Pageable pageable);
    Page<Shipment> findByLogisticsPartnerId(Long partnerId, Pageable pageable);
}
