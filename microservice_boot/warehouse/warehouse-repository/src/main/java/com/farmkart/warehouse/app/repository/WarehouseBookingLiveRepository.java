package com.farmkart.warehouse.app.repository;

import com.farmkart.warehouse.app.entity.WarehouseBookingLive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WarehouseBookingLiveRepository extends JpaRepository<WarehouseBookingLive, Long> {
    Optional<WarehouseBookingLive> findByBookingId(Long bookingId);
    Optional<WarehouseBookingLive> findByFarmerId(Long farmerId);
}
