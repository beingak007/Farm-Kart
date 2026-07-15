package com.farmkart.warehouse.app.repository;

import com.farmkart.warehouse.app.entity.WarehouseComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WarehouseCommentRepository extends JpaRepository<WarehouseComment, Long> {
    List<WarehouseComment> findByWarehouseIdOrderByCreatedAtDesc(Long warehouseId);
}
