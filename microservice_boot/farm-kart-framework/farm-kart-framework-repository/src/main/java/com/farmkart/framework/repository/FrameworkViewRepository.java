package com.farmkart.framework.repository;

import com.farmkart.framework.client.enums.FkViewType;
import com.farmkart.framework.repository.entity.FrameworkView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FrameworkViewRepository extends JpaRepository<FrameworkView, Long> {

    Optional<FrameworkView> findByModel_ModelNameAndViewName(String modelName, String viewName);

    List<FrameworkView> findByModel_ModelName(String modelName);

    List<FrameworkView> findByModel_ModelNameAndViewType(String modelName, FkViewType viewType);

    @Query("SELECT v FROM FrameworkView v JOIN FETCH v.model WHERE v.viewType = :viewType")
    List<FrameworkView> findAllByViewType(@Param("viewType") FkViewType viewType);
}
