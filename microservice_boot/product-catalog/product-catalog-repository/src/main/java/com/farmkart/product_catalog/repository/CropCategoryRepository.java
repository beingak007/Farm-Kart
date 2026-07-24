package com.farmkart.product_catalog.repository;

import com.farmkart.product_catalog.repository.entity.CropCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CropCategoryRepository extends JpaRepository<CropCategory, Long> {
    Optional<CropCategory> findBySlug(String slug);
    List<CropCategory> findByIsActiveOrderByNameAsc(Boolean isActive);
    List<CropCategory> findByParentCategoryId(Long parentId);
}
