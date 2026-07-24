package com.farmkart.warehouse.client.dto;

import java.time.Instant;
import java.util.List;

/** Pre-booking warehouse profile: photos, ratings, comments. */
public record WarehouseDetailResponse(
        WarehouseResponse warehouse,
        double averageRating,
        long ratingCount,
        List<WarehousePhotoResponse> photos,
        List<WarehouseRatingResponse> ratings,
        List<WarehouseCommentResponse> comments
) {}
