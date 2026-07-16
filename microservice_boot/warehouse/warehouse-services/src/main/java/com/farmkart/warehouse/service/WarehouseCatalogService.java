package com.farmkart.warehouse.service;

import com.farmkart.starter.common.exception.BusinessException;
import com.farmkart.warehouse.app.entity.WarehouseComment;
import com.farmkart.warehouse.app.entity.WarehousePhoto;
import com.farmkart.warehouse.app.entity.WarehouseRating;
import com.farmkart.warehouse.app.repository.WarehouseCommentRepository;
import com.farmkart.warehouse.app.repository.WarehousePhotoRepository;
import com.farmkart.warehouse.app.repository.WarehouseRatingRepository;
import com.farmkart.warehouse.client.dto.*;
import com.farmkart.warehouse.repository.WarehouseRepository;
import com.farmkart.warehouse.repository.entity.Warehouse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Pre-booking catalog: photos, ratings, comments (PostgreSQL app data).
 */
@Service
public class WarehouseCatalogService {

    private final WarehouseRepository warehouseRepo;
    private final WarehousePhotoRepository photoRepo;
    private final WarehouseRatingRepository ratingRepo;
    private final WarehouseCommentRepository commentRepo;
    private final WarehouseS3Service s3Service;

    public WarehouseCatalogService(WarehouseRepository warehouseRepo,
                                   WarehousePhotoRepository photoRepo,
                                   WarehouseRatingRepository ratingRepo,
                                   WarehouseCommentRepository commentRepo,
                                   WarehouseS3Service s3Service) {
        this.warehouseRepo = warehouseRepo;
        this.photoRepo = photoRepo;
        this.ratingRepo = ratingRepo;
        this.commentRepo = commentRepo;
        this.s3Service = s3Service;
    }

    @Transactional(value = "warehouseAppTransactionManager", readOnly = true)
    public WarehouseDetailResponse getDetail(Long warehouseId) {
        Warehouse w = warehouseRepo.findById(warehouseId)
                .orElseThrow(() -> new BusinessException(404, "Warehouse not found"));

        Double avg = ratingRepo.averageStars(warehouseId);
        long count = ratingRepo.countByWarehouse(warehouseId);

        List<WarehousePhotoResponse> photos = photoRepo.findByWarehouseIdOrderBySortOrderAsc(warehouseId)
                .stream().map(this::toPhoto).toList();
        List<WarehouseRatingResponse> ratings = ratingRepo.findByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                .stream().map(this::toRating).toList();
        List<WarehouseCommentResponse> comments = commentRepo.findByWarehouseIdOrderByCreatedAtDesc(warehouseId)
                .stream().map(this::toComment).toList();

        WarehouseResponse base = new WarehouseResponse(
                w.getId(), w.getName(), w.getAddress(), w.getState(), w.getDistrict(), w.getPincode(),
                w.getTotalCapacityTons(), w.getAvailableCapacityTons(), w.isColdStorage(), w.getStatus());

        return new WarehouseDetailResponse(
                base,
                avg != null ? avg : 0.0,
                count,
                photos,
                ratings,
                comments);
    }

    @Transactional("warehouseAppTransactionManager")
    public WarehousePhotoResponse addPhoto(Long warehouseId, AddWarehousePhotoRequest req) {
        assertWarehouseExists(warehouseId);
        WarehousePhoto photo = new WarehousePhoto();
        photo.setWarehouseId(warehouseId);
        photo.setS3Key(req.s3Key());
        photo.setCaption(req.caption());
        photo.setSortOrder(req.sortOrder());
        return toPhoto(photoRepo.save(photo));
    }

    @Transactional("warehouseAppTransactionManager")
    public WarehouseRatingResponse addRating(Long warehouseId, WarehouseRatingRequest req) {
        assertWarehouseExists(warehouseId);
        if (ratingRepo.findByWarehouseIdAndFarmerId(warehouseId, req.farmerId()).isPresent()) {
            throw new BusinessException(409, "Farmer already rated this warehouse");
        }
        WarehouseRating rating = new WarehouseRating();
        rating.setWarehouseId(warehouseId);
        rating.setFarmerId(req.farmerId());
        rating.setStars(req.stars().shortValue());
        return toRating(ratingRepo.save(rating));
    }

    @Transactional("warehouseAppTransactionManager")
    public WarehouseCommentResponse addComment(Long warehouseId, WarehouseCommentRequest req) {
        assertWarehouseExists(warehouseId);
        WarehouseComment comment = new WarehouseComment();
        comment.setWarehouseId(warehouseId);
        comment.setFarmerId(req.farmerId());
        comment.setRatingId(req.ratingId());
        comment.setBody(req.body());
        return toComment(commentRepo.save(comment));
    }

    @Transactional(value = "warehouseAppTransactionManager", readOnly = true)
    public double averageRating(Long warehouseId) {
        Double avg = ratingRepo.averageStars(warehouseId);
        return avg != null ? avg : 0.0;
    }

    private void assertWarehouseExists(Long warehouseId) {
        if (!warehouseRepo.existsById(warehouseId)) {
            throw new BusinessException(404, "Warehouse not found");
        }
    }

    private WarehousePhotoResponse toPhoto(WarehousePhoto p) {
        return new WarehousePhotoResponse(
                p.getId(), s3Service.presignedUrl(p.getS3Key()), p.getCaption(), p.getSortOrder(), p.getCreatedAt());
    }

    private WarehouseRatingResponse toRating(WarehouseRating r) {
        return new WarehouseRatingResponse(r.getId(), r.getFarmerId(), r.getStars(), r.getCreatedAt());
    }

    private WarehouseCommentResponse toComment(WarehouseComment c) {
        return new WarehouseCommentResponse(c.getId(), c.getFarmerId(), c.getRatingId(), c.getBody(), c.getCreatedAt());
    }
}
