package com.farmkart.warehouse.repository;

import com.farmkart.warehouse.repository.entity.Warehouse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Page<Warehouse> findByStateAndStatus(String state, String status, Pageable pageable);
    Page<Warehouse> findByColdStorageAndStatus(boolean coldStorage, String status, Pageable pageable);

    @Query("SELECT w FROM Warehouse w WHERE w.state = :state AND w.availableCapacityTons >= :needed AND w.status = 'ACTIVE'")
    List<Warehouse> findAvailableInState(@Param("state") String state, @Param("needed") double needed);

    @Query("SELECT w FROM Warehouse w WHERE w.status = :status AND w.availableCapacityTons >= :needed")
    List<Warehouse> findAvailableWithCapacity(@Param("status") String status, @Param("needed") double needed);
}
