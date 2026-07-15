package com.farmkart.warehouse.app.repository;

import com.farmkart.warehouse.app.entity.WarehousePhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehousePhotoRepository extends JpaRepository<WarehousePhoto, Long> {
    List<WarehousePhoto> findByWarehouseIdOrderBySortOrderAsc(Long warehouseId);
}
