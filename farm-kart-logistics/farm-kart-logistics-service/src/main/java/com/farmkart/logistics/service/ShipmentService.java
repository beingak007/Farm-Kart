package com.farmkart.logistics.service;

import com.farmkart.logistics.client.dto.CreateShipmentRequest;
import com.farmkart.logistics.client.dto.ShipmentResponse;
import com.farmkart.logistics.repository.ShipmentRepository;
import com.farmkart.logistics.repository.entity.Shipment;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.events.ShipmentCreatedEvent;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepo;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ShipmentService(ShipmentRepository shipmentRepo, KafkaTemplate<String, Object> kafkaTemplate) {
        this.shipmentRepo = shipmentRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public ShipmentResponse createShipment(CreateShipmentRequest req) {
        Shipment s = new Shipment();
        s.setOrderId(req.orderId());
        s.setLogisticsPartnerId(req.logisticsPartnerId());
        s.setPickupAddress(req.pickupAddress());
        s.setDeliveryAddress(req.deliveryAddress());
        s.setWeightKg(req.weightKg());
        s.setExpectedDelivery(req.expectedDelivery());
        s.setNotes(req.notes());
        s.setTrackingNumber("FK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        s = shipmentRepo.save(s);

        ShipmentCreatedEvent event = new ShipmentCreatedEvent(
                new FkBaseEvent(FkTopics.SHIPMENT_CREATED, "logistics-service"),
                s.getId(), s.getOrderId(), s.getLogisticsPartnerId(),
                s.getTrackingNumber(), s.getPickupAddress(), s.getDeliveryAddress(),
                s.getExpectedDelivery());
        kafkaTemplate.send(FkTopics.SHIPMENT_CREATED, String.valueOf(s.getId()), event);
        return toResponse(s);
    }

    @Transactional(readOnly = true)
    public ShipmentResponse track(String trackingNumber) {
        return toResponse(shipmentRepo.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new BusinessException(404, "Shipment not found: " + trackingNumber)));
    }

    @Transactional(readOnly = true)
    public List<ShipmentResponse> getByOrderId(Long orderId) {
        return shipmentRepo.findByOrderId(orderId).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Page<ShipmentResponse> listByStatus(String status, Pageable pageable) {
        return shipmentRepo.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional
    public ShipmentResponse updateStatus(Long shipmentId, String newStatus) {
        Shipment s = shipmentRepo.findById(shipmentId)
                .orElseThrow(() -> new BusinessException(404, "Shipment not found: " + shipmentId));
        s.setStatus(newStatus);
        if ("DELIVERED".equals(newStatus)) {
            s.setActualDelivery(Instant.now());
            kafkaTemplate.send(FkTopics.SHIPMENT_DELIVERED, String.valueOf(shipmentId),
                    new FkBaseEvent(FkTopics.SHIPMENT_DELIVERED, "logistics-service"));
        }
        return toResponse(shipmentRepo.save(s));
    }

    private ShipmentResponse toResponse(Shipment s) {
        return new ShipmentResponse(s.getId(), s.getOrderId(), s.getLogisticsPartnerId(),
                s.getTrackingNumber(), s.getPickupAddress(), s.getDeliveryAddress(),
                s.getStatus(), s.getExpectedDelivery(), s.getActualDelivery(),
                s.getWeightKg(), s.getNotes(), s.getCreatedAt());
    }
}
