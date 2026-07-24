package com.farmkart.warehouse.service;

import com.farmkart.starter.common.geo.GeoDistanceUtil;
import com.farmkart.warehouse.client.dto.NearbyWarehouseResponse;
import com.farmkart.warehouse.constants.WarehouseServiceConstants;
import com.farmkart.warehouse.enums.WarehouseStatusEnum;
import com.farmkart.warehouse.repository.WarehouseRepository;
import com.farmkart.warehouse.repository.entity.Warehouse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Uber-style warehouse matching: find available storage within a radius, nearest first.
 *
 * <p>Algorithm:
 * <ol>
 *   <li>Load all ACTIVE warehouses with enough free capacity</li>
 *   <li>Keep only those with geo coordinates within {@code radiusKm} (default 100 km)</li>
 *   <li>Sort by distance ascending — nearest warehouse wins (like Uber driver match)</li>
 *   <li>Compute rent = days × tons × pricePerTonPerDay × cold-storage multiplier</li>
 * </ol>
 */
@Component
public class WarehouseNearbyMatchingService {

    private final WarehouseRepository warehouseRepo;

    public WarehouseNearbyMatchingService(WarehouseRepository warehouseRepo) {
        this.warehouseRepo = warehouseRepo;
    }

    public List<NearbyWarehouseResponse> findAvailableWithinRadius(
            double pickupLat, double pickupLng, double quantityTons,
            LocalDate startDate, LocalDate endDate, double radiusKm) {

        return warehouseRepo
                .findAvailableWithCapacity(WarehouseStatusEnum.ACTIVE.getValue(), quantityTons)
                .stream()
                .filter(w -> w.getLatitude() != null && w.getLongitude() != null)
                .map(w -> toNearby(w, pickupLat, pickupLng, quantityTons, startDate, endDate))
                .filter(n -> n.distanceKm() <= radiusKm)
                .sorted(Comparator.comparingDouble(NearbyWarehouseResponse::distanceKm))
                .toList();
    }

    /**
     * Returns the nearest warehouse match within radius, or empty if none available.
     */
    public Optional<NearbyWarehouseResponse> selectNearestMatch(
            double pickupLat, double pickupLng, double quantityTons,
            LocalDate startDate, LocalDate endDate, double radiusKm) {

        List<NearbyWarehouseResponse> matches = findAvailableWithinRadius(
                pickupLat, pickupLng, quantityTons, startDate, endDate, radiusKm);
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.getFirst());
    }

    public BigDecimal calculateRent(Warehouse warehouse, double quantityTons,
                                    LocalDate startDate, LocalDate endDate) {
        long days = Math.max(1, ChronoUnit.DAYS.between(startDate, endDate));
        BigDecimal rate = warehouse.getPricePerTonPerDay() != null
                ? warehouse.getPricePerTonPerDay()
                : new BigDecimal("5.00");
        if (warehouse.isColdStorage()) {
            rate = rate.multiply(WarehouseServiceConstants.COLD_STORAGE_RATE_MULTIPLIER);
        }
        return rate
                .multiply(BigDecimal.valueOf(quantityTons))
                .multiply(BigDecimal.valueOf(days))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private NearbyWarehouseResponse toNearby(Warehouse w, double pickupLat, double pickupLng,
                                               double quantityTons, LocalDate startDate, LocalDate endDate) {
        double distanceKm = GeoDistanceUtil.haversineKm(
                pickupLat, pickupLng, w.getLatitude(), w.getLongitude());
        BigDecimal rent = calculateRent(w, quantityTons, startDate, endDate);
        return new NearbyWarehouseResponse(
                w.getId(), w.getName(), w.getAddress(), w.getState(), w.getDistrict(), w.getPincode(),
                w.getTotalCapacityTons(), w.getAvailableCapacityTons(), w.isColdStorage(), w.getStatus(),
                w.getLatitude(), w.getLongitude(), w.getPricePerTonPerDay(),
                roundDistance(distanceKm), rent);
    }

    private static double roundDistance(double km) {
        return BigDecimal.valueOf(km).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
