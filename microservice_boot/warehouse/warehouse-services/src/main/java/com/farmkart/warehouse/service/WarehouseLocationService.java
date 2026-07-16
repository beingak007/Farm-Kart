package com.farmkart.warehouse.service;

import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.WarehouseLiveLocationEvent;
import com.farmkart.starter.common.exception.BusinessException;
import com.farmkart.warehouse.app.entity.WarehouseBookingLive;
import com.farmkart.warehouse.app.repository.WarehouseBookingLiveRepository;
import com.farmkart.warehouse.client.dto.BookingLiveLocationResponse;
import com.farmkart.warehouse.client.dto.PostBookingLocationRequest;
import com.farmkart.warehouse.constants.WarehouseServiceConstants;
import com.farmkart.warehouse.repository.WarehouseBookingRepository;
import com.farmkart.warehouse.repository.WarehouseRepository;
import com.farmkart.warehouse.repository.entity.Warehouse;
import com.farmkart.warehouse.repository.entity.WarehouseBooking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class WarehouseLocationService {

    private final WarehouseBookingLiveRepository liveRepo;
    private final WarehouseBookingRepository bookingRepo;
    private final WarehouseRepository warehouseRepo;
    private final DomainEventPublisher eventPublisher;

    public WarehouseLocationService(WarehouseBookingLiveRepository liveRepo,
                                    WarehouseBookingRepository bookingRepo,
                                    WarehouseRepository warehouseRepo,
                                    DomainEventPublisher eventPublisher) {
        this.liveRepo = liveRepo;
        this.bookingRepo = bookingRepo;
        this.warehouseRepo = warehouseRepo;
        this.eventPublisher = eventPublisher;
    }

    public void publishInitialLocation(WarehouseBooking booking, Warehouse warehouse, String farmerContact) {
        if (warehouse.getLatitude() == null || warehouse.getLongitude() == null) {
            return;
        }
        saveLiveAndPublish(
                booking.getId(), warehouse.getId(), booking.getFarmerId(), warehouse.getName(),
                warehouse.getLatitude(), warehouse.getLongitude(), farmerContact);
    }

    @Transactional(readOnly = true)
    public BookingLiveLocationResponse updateLocation(Long bookingId, PostBookingLocationRequest req) {
        WarehouseBooking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new BusinessException(404, "Booking not found"));
        Warehouse warehouse = warehouseRepo.findById(booking.getWarehouse().getId())
                .orElseThrow(() -> new BusinessException(404, "Warehouse not found"));

        saveLiveAndPublish(
                booking.getId(), warehouse.getId(), booking.getFarmerId(), warehouse.getName(),
                req.latitude(), req.longitude(), null);
        return getLiveLocation(bookingId);
    }

    @Transactional(value = "warehouseAppTransactionManager", readOnly = true)
    public BookingLiveLocationResponse getLiveLocation(Long bookingId) {
        WarehouseBookingLive live = liveRepo.findByBookingId(bookingId)
                .orElseThrow(() -> new BusinessException(404, "Live location not available"));
        return toResponse(live);
    }

    @Transactional(value = "warehouseAppTransactionManager", readOnly = true)
    public BookingLiveLocationResponse getLiveLocationForFarmer(Long farmerId) {
        WarehouseBookingLive live = liveRepo.findByFarmerId(farmerId)
                .orElseThrow(() -> new BusinessException(404, "No active booking location"));
        return toResponse(live);
    }

    @Transactional(value = "warehouseAppTransactionManager", propagation = Propagation.REQUIRES_NEW)
    public void saveLiveAndPublish(Long bookingId, Long warehouseId, Long farmerId, String warehouseName,
                                   double lat, double lng, String farmerContact) {
        WarehouseBookingLive live = liveRepo.findByBookingId(bookingId)
                .orElse(new WarehouseBookingLive());
        live.setBookingId(bookingId);
        live.setWarehouseId(warehouseId);
        live.setFarmerId(farmerId);
        live.setLatitude(lat);
        live.setLongitude(lng);
        liveRepo.save(live);

        String mapsUrl = "https://maps.google.com/?q=" + lat + "," + lng;
        WarehouseLiveLocationEvent event = new WarehouseLiveLocationEvent(
                new FkBaseEvent(FkTopics.WAREHOUSE_LIVE_LOCATION, WarehouseServiceConstants.SERVICE_ORIGIN),
                bookingId, warehouseId, warehouseName, farmerId, farmerContact,
                lat, lng, mapsUrl, Instant.now());
        eventPublisher.publish(FkTopics.WAREHOUSE_LIVE_LOCATION, String.valueOf(bookingId), event);
    }

    private BookingLiveLocationResponse toResponse(WarehouseBookingLive live) {
        String mapsUrl = "https://maps.google.com/?q=" + live.getLatitude() + "," + live.getLongitude();
        return new BookingLiveLocationResponse(
                live.getBookingId(), live.getWarehouseId(),
                live.getLatitude(), live.getLongitude(), mapsUrl, live.getUpdatedAt());
    }
}
