package com.farmkart.product_catalog.repository;

import com.farmkart.product_catalog.repository.entity.Crop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CropRepository extends JpaRepository<Crop, Long> {
    Optional<Crop> findBySlug(String slug);
    Page<Crop> findByCategoryIdAndIsActive(Long categoryId, Boolean isActive, Pageable pageable);
    Page<Crop> findByIsOrganicAndIsActive(Boolean isOrganic, Boolean isActive, Pageable pageable);

    @Query("SELECT c FROM Crop c WHERE c.isActive = true AND " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%',:q,'%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<Crop> search(@Param("q") String query, Pageable pageable);
}
