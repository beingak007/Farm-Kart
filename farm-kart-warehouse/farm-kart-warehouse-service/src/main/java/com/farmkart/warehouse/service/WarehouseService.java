package com.farmkart.warehouse.service;

import com.farmkart.warehouse.client.dto.BookWarehouseRequest;
import com.farmkart.warehouse.client.dto.WarehouseResponse;
import com.farmkart.warehouse.repository.WarehouseBookingRepository;
import com.farmkart.warehouse.repository.WarehouseRepository;
import com.farmkart.warehouse.repository.entity.Warehouse;
import com.farmkart.warehouse.repository.entity.WarehouseBooking;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class WarehouseService {

    private static final BigDecimal COST_PER_TON_PER_DAY = new BigDecimal("5.00");

    private final WarehouseRepository warehouseRepo;
    private final WarehouseBookingRepository bookingRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public WarehouseService(WarehouseRepository warehouseRepo,
                             WarehouseBookingRepository bookingRepo,
                             KafkaTemplate<String, Object> kafkaTemplate) {
        this.warehouseRepo = warehouseRepo;
        this.bookingRepo = bookingRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional(readOnly = true)
    public Page<WarehouseResponse> listByState(String state, Pageable pageable) {
        return warehouseRepo.findByStateAndStatus(state, "ACTIVE", pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public List<WarehouseResponse> findAvailable(String state, double tons) {
        return warehouseRepo.findAvailableInState(state, tons).stream().map(this::toResponse).toList();
    }

    @Transactional
    public Long book(BookWarehouseRequest req) {
        Warehouse w = warehouseRepo.findById(req.warehouseId())
                .orElseThrow(() -> new BusinessException(404, "Warehouse not found: " + req.warehouseId()));
        if (w.getAvailableCapacityTons() < req.quantityTons()) {
            throw new BusinessException(400, "Insufficient warehouse capacity");
        }
        long days = ChronoUnit.DAYS.between(req.startDate(), req.endDate());
        BigDecimal cost = COST_PER_TON_PER_DAY
                .multiply(BigDecimal.valueOf(req.quantityTons()))
                .multiply(BigDecimal.valueOf(days));

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

        kafkaTemplate.send(FkTopics.WAREHOUSE_BOOKED, String.valueOf(booking.getId()),
                new FkBaseEvent(FkTopics.WAREHOUSE_BOOKED, "warehouse-service"));
        return booking.getId();
    }

    private WarehouseResponse toResponse(Warehouse w) {
        return new WarehouseResponse(w.getId(), w.getName(), w.getAddress(), w.getState(),
                w.getDistrict(), w.getPincode(), w.getTotalCapacityTons(),
                w.getAvailableCapacityTons(), w.isColdStorage(), w.getStatus());
    }
}
