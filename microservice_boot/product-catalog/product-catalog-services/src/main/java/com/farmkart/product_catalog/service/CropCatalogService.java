package com.farmkart.product_catalog.service;

import com.farmkart.product_catalog.client.dto.CreateCategoryRequest;
import com.farmkart.product_catalog.client.dto.CreateCropRequest;
import com.farmkart.product_catalog.client.dto.CropResponse;
import com.farmkart.product_catalog.repository.CropCategoryRepository;
import com.farmkart.product_catalog.repository.CropRepository;
import com.farmkart.product_catalog.repository.entity.Crop;
import com.farmkart.product_catalog.repository.entity.CropCategory;
import com.farmkart.starter.common.cache.FkCacheNames;
import com.farmkart.product_catalog.constants.ProductCatalogServiceConstants;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CropCatalogService {

    private final CropRepository cropRepo;
    private final CropCategoryRepository categoryRepo;
    private final DomainEventPublisher eventPublisher;

    public CropCatalogService(CropRepository cropRepo,
                               CropCategoryRepository categoryRepo,
                               DomainEventPublisher eventPublisher) {
        this.cropRepo = cropRepo;
        this.categoryRepo = categoryRepo;
        this.eventPublisher = eventPublisher;
    }

    // ── Categories ────────────────────────────────────────────────────────

    @Transactional
    @CacheEvict(cacheNames = FkCacheNames.PRODUCT_CATEGORY, allEntries = true)
    public Long createCategory(CreateCategoryRequest req) {
        CropCategory cat = new CropCategory();
        cat.setName(req.name());
        cat.setSlug(req.slug());
        cat.setDescription(req.description());
        cat.setParentCategoryId(req.parentCategoryId());
        cat.setImageUrl(req.imageUrl());
        return categoryRepo.save(cat).getId();
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = FkCacheNames.PRODUCT_CATEGORY)
    public List<CropCategory> listActiveCategories() {
        return categoryRepo.findByIsActiveOrderByNameAsc(true);
    }

    // ── Crops ─────────────────────────────────────────────────────────────

    @Transactional
    public CropResponse createCrop(CreateCropRequest req) {
        CropCategory cat = categoryRepo.findById(req.categoryId())
                .orElseThrow(() -> new BusinessException(404, "Category not found: " + req.categoryId()));
        Crop crop = new Crop();
        crop.setName(req.name());
        crop.setSlug(req.slug());
        crop.setCategory(cat);
        crop.setDescription(req.description());
        crop.setImageUrl(req.imageUrl());
        crop.setUnit(req.unit());
        crop.setBasePrice(req.basePrice());
        crop.setHarvestSeason(req.harvestSeason());
        crop.setGradeStandard(req.gradeStandard());
        if (req.isOrganic() != null) crop.setIsOrganic(req.isOrganic());
        crop = cropRepo.save(crop);
        eventPublisher.publish(FkTopics.CROP_LISTED, String.valueOf(crop.getId()),
                new FkBaseEvent(FkTopics.CROP_LISTED, ProductCatalogServiceConstants.SERVICE_NAME));
        return toResponse(crop);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = FkCacheNames.PRODUCT_BY_ID, key = "#cropId")
    public CropResponse getById(Long cropId) {
        return toResponse(cropRepo.findById(cropId)
                .orElseThrow(() -> new BusinessException(404, "Crop not found: " + cropId)));
    }

    @Transactional(readOnly = true)
    public CropResponse getBySlug(String slug) {
        return toResponse(cropRepo.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(404, "Crop not found: " + slug)));
    }

    @Transactional(readOnly = true)
    public Page<CropResponse> search(String query, Pageable pageable) {
        return cropRepo.search(query, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CropResponse> listByCategory(Long categoryId, Pageable pageable) {
        return cropRepo.findByCategoryIdAndIsActive(categoryId, true, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CropResponse> listOrganic(Pageable pageable) {
        return cropRepo.findByIsOrganicAndIsActive(true, true, pageable).map(this::toResponse);
    }

    private CropResponse toResponse(Crop c) {
        return new CropResponse(c.getId(), c.getName(), c.getSlug(),
                c.getCategory().getId(), c.getCategory().getName(),
                c.getDescription(), c.getImageUrl(), c.getUnit(), c.getBasePrice(),
                c.getHarvestSeason(), c.getGradeStandard(), c.getIsOrganic(), c.getIsActive(), c.getCreatedAt());
    }
}
