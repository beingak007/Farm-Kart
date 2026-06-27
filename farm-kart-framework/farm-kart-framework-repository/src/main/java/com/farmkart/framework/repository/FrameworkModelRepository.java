package com.farmkart.framework.repository;

import com.farmkart.framework.repository.entity.FrameworkModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrameworkModelRepository extends JpaRepository<FrameworkModel, Long> {

    Optional<FrameworkModel> findByModelName(String modelName);
}
