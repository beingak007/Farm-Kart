package com.farmkart.warehouse.repository;

import com.farmkart.warehouse.repository.entity.WarehouseBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseBookingRepository extends JpaRepository<WarehouseBooking, Long> {
    Page<WarehouseBooking> findByFarmerId(Long farmerId, Pageable pageable);
    Page<WarehouseBooking> findByWarehouse_Id(Long warehouseId, Pageable pageable);
}
