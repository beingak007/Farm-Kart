package com.farmkart.repository;

import com.farmkart.repository.entity.SheetUpload;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SheetUploadRepository extends JpaRepository<SheetUpload, Long> {

    List<SheetUpload> findByUserIdOrderByCreatedAtDesc(Long userId);
}
