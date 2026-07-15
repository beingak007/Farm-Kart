package com.farmkart.warehouse.app.repository;

import com.farmkart.warehouse.app.entity.WarehouseRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WarehouseRatingRepository extends JpaRepository<WarehouseRating, Long> {
    List<WarehouseRating> findByWarehouseIdOrderByCreatedAtDesc(Long warehouseId);
    Optional<WarehouseRating> findByWarehouseIdAndFarmerId(Long warehouseId, Long farmerId);

    @Query("SELECT AVG(r.stars) FROM WarehouseRating r WHERE r.warehouseId = :warehouseId")
    Double averageStars(@Param("warehouseId") Long warehouseId);

    @Query("SELECT COUNT(r) FROM WarehouseRating r WHERE r.warehouseId = :warehouseId")
    long countByWarehouse(@Param("warehouseId") Long warehouseId);
}
