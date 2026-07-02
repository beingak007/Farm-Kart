package com.farmkart.warehouse.service;

import com.farmkart.starter.common.constants.FkCurrencyConstants;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.WarehouseBookedEvent;
import com.farmkart.starter.common.exception.BusinessException;
import com.farmkart.warehouse.client.dto.*;
import com.farmkart.warehouse.constants.WarehouseServiceConstants;
import com.farmkart.warehouse.enums.WarehouseStatusEnum;
import com.farmkart.warehouse.repository.WarehouseBookingRepository;
import com.farmkart.warehouse.repository.WarehouseRepository;
import com.farmkart.warehouse.repository.entity.Warehouse;
import com.farmkart.warehouse.repository.entity.WarehouseBooking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepo;
    private final WarehouseBookingRepository bookingRepo;
    private final DomainEventPublisher eventPublisher;
    private final WarehouseNearbyMatchingService matchingService;
    private final WarehouseLocationService locationService;

    public WarehouseService(WarehouseRepository warehouseRepo,
                             WarehouseBookingRepository bookingRepo,
                             DomainEventPublisher eventPublisher,
                             WarehouseNearbyMatchingService matchingService,
                             WarehouseLocationService locationService) {
        this.warehouseRepo = warehouseRepo;
        this.bookingRepo = bookingRepo;
        this.eventPublisher = eventPublisher;
        this.matchingService = matchingService;
        this.locationService = locationService;
    }

    @Transactional(readOnly = true)
    public Page<WarehouseResponse> listByState(String state, Pageable pageable) {
        return warehouseRepo.findByStateAndStatus(state, WarehouseStatusEnum.ACTIVE.getValue(), pageable)
                .map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> findAvailable(String state, double tons) {
        return warehouseRepo.findAvailableInState(state, tons).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<NearbyWarehouseResponse> findNearby(double lat, double lng, double tons,
                                                    java.time.LocalDate startDate, java.time.LocalDate endDate,
                                                    Double radiusKm) {
        double radius = radiusKm != null ? radiusKm : WarehouseServiceConstants.DEFAULT_MATCH_RADIUS_KM;
        return matchingService.findAvailableWithinRadius(lat, lng, tons, startDate, endDate, radius);
    }

    /**
     * Uber-style smart book: nearest available warehouse within 100 km, auto-confirm + notify.
     */
    @Transactional
    public SmartBookWarehouseResponse smartBook(SmartBookWarehouseRequest req) {
        double radius = req.radiusKm() != null ? req.radiusKm() : WarehouseServiceConstants.DEFAULT_MATCH_RADIUS_KM;
        String currency = req.currency() != null ? req.currency() : FkCurrencyConstants.DEFAULT_CODE;

        var match = matchingService.selectNearestMatch(
                req.pickupLatitude(), req.pickupLongitude(), req.quantityTons(),
                req.startDate(), req.endDate(), radius)
                .orElseThrow(() -> new BusinessException(404,
                        "No warehouse available within " + (int) radius + " km for "
                                + req.quantityTons() + " tons"));

        Warehouse w = warehouseRepo.findById(match.id())
                .orElseThrow(() -> new BusinessException(404, "Warehouse not found"));

        if (w.getAvailableCapacityTons() < req.quantityTons()) {
            throw new BusinessException(409, "Warehouse capacity changed — please retry");
        }

        BigDecimal rent = matchingService.calculateRent(w, req.quantityTons(), req.startDate(), req.endDate());

        w.setAvailableCapacityTons(w.getAvailableCapacityTons() - req.quantityTons());
        warehouseRepo.save(w);

        WarehouseBooking booking = new WarehouseBooking();
        booking.setWarehouse(w);
        booking.setFarmerId(req.farmerId());
        booking.setCropName(req.cropName());
        booking.setQuantityTons(req.quantityTons());
        booking.setStartDate(req.startDate());
        booking.setEndDate(req.endDate());
        booking.setTotalCost(rent);
        booking.setDistanceKm(match.distanceKm());
        booking.setPickupLatitude(req.pickupLatitude());
        booking.setPickupLongitude(req.pickupLongitude());
        booking = bookingRepo.save(booking);

        publishBookedEvent(booking, w, req.farmerContact(), currency, match.distanceKm());
        locationService.publishInitialLocation(booking, w, req.farmerContact());

        return new SmartBookWarehouseResponse(
                booking.getId(), w.getId(), w.getName(), w.getAddress(),
                match.distanceKm(), rent, currency, booking.getStatus());
    }

    @Transactional
    public Long book(BookWarehouseRequest req) {
        Warehouse w = warehouseRepo.findById(req.warehouseId())
                .orElseThrow(() -> new BusinessException(404, "Warehouse not found: " + req.warehouseId()));
        if (w.getAvailableCapacityTons() < req.quantityTons()) {
            throw new BusinessException(400, "Insufficient warehouse capacity");
        }
        BigDecimal cost = matchingService.calculateRent(w, req.quantityTons(), req.startDate(), req.endDate());

        w.setAvailableCapacityTons(w.getAvailableCapacityTons() - req.quantityTons());
        warehouseRepo.save(w);

        WarehouseBooking booking = new WarehouseBooking();
        booking.setWarehouse(w);
        booking.setFarmerId(req.farmerId());
        booking.setCropName(req.cropName());
        booking.setQuantityTons(req.quantityTons());
        booking.setStartDate(req.startDate());
        booking.setEndDate(req.endDate());
        booking.setTotalCost(cost);
        booking = bookingRepo.save(booking);

        publishBookedEvent(booking, w, null, FkCurrencyConstants.DEFAULT_CODE, null);
        return booking.getId();
    }

    private void publishBookedEvent(WarehouseBooking booking, Warehouse w, String farmerContact,
                                    String currency, Double distanceKm) {
        WarehouseBookedEvent event = new WarehouseBookedEvent(
                new FkBaseEvent(FkTopics.WAREHOUSE_BOOKED, WarehouseServiceConstants.SERVICE_ORIGIN),
                booking.getId(),
                w.getId(),
                w.getName(),
                w.getAddress(),
                booking.getFarmerId(),
                farmerContact,
                booking.getCropName(),
                booking.getQuantityTons(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getTotalCost(),
                currency,
                distanceKm,
                Instant.now());
        eventPublisher.publish(FkTopics.WAREHOUSE_BOOKED, String.valueOf(booking.getId()), event);
    }

    private WarehouseResponse toResponse(Warehouse w) {
        return new WarehouseResponse(w.getId(), w.getName(), w.getAddress(), w.getState(),
                w.getDistrict(), w.getPincode(), w.getTotalCapacityTons(),
                w.getAvailableCapacityTons(), w.isColdStorage(), w.getStatus());
    }
}
