package com.farmkart.farmer.service;

import com.farmkart.farmer.client.dto.FarmerOnboardRequest;
import com.farmkart.farmer.client.dto.FarmerResponse;
import com.farmkart.farmer.client.dto.FarmUpdateRequest;
import com.farmkart.farmer.client.enums.FarmerStatus;
import com.farmkart.farmer.repository.FarmerRepository;
import com.farmkart.farmer.repository.entity.Farmer;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FarmerCreatedEvent;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class FarmerService {

    private final FarmerRepository farmerRepo;
    private final DomainEventPublisher eventPublisher;

    public FarmerService(FarmerRepository farmerRepo,
                         DomainEventPublisher eventPublisher) {
        this.farmerRepo = farmerRepo;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public FarmerResponse onboard(FarmerOnboardRequest req) {
        if (farmerRepo.existsByUserId(req.userId())) {
            throw new BusinessException(409, "Farmer profile already exists for userId: " + req.userId());
        }
        Farmer farmer = new Farmer();
        farmer.setUserId(req.userId());
        farmer.setFarmName(req.farmName());
        farmer.setAddress(req.address());
        farmer.setState(req.state());
        farmer.setDistrict(req.district());
        farmer.setPincode(req.pincode());
        farmer.setFarmAreaAcres(req.farmAreaAcres());
        farmer.setPrimaryCrop(req.primaryCrop());
        farmer.setBankAccount(req.bankAccount());
        farmer.setIfsc(req.ifsc());
        farmer.setAadhaarNumber(req.aadhaarNumber());
        farmer.setPanNumber(req.panNumber());
        farmer = farmerRepo.save(farmer);

        FarmerCreatedEvent event = new FarmerCreatedEvent(
                new FkBaseEvent(FkTopics.FARMER_CREATED, "farmer-service"),
                farmer.getId(), farmer.getUserId(), farmer.getFarmName(),
                farmer.getState(), farmer.getDistrict(), Instant.now());
        eventPublisher.publish(FkTopics.FARMER_CREATED, String.valueOf(farmer.getId()), event);

        return toResponse(farmer);
    }

    @Transactional(readOnly = true)
    public FarmerResponse getByUserId(Long userId) {
        Farmer farmer = farmerRepo.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(404, "Farmer not found for userId: " + userId));
        return toResponse(farmer);
    }

    @Transactional(readOnly = true)
    public FarmerResponse getById(Long farmerId) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new BusinessException(404, "Farmer not found: " + farmerId));
        return toResponse(farmer);
    }

    @Transactional(readOnly = true)
    public Page<FarmerResponse> listByState(String state, Pageable pageable) {
        return farmerRepo.findByState(state, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FarmerResponse> listByStatus(FarmerStatus status, Pageable pageable) {
        return farmerRepo.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<FarmerResponse> listAll(Pageable pageable) {
        return farmerRepo.findAll(pageable).map(this::toResponse);
    }

    @Transactional
    public FarmerResponse updateFarm(Long farmerId, FarmUpdateRequest req) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new BusinessException(404, "Farmer not found: " + farmerId));
        if (req.farmName()    != null) farmer.setFarmName(req.farmName());
        if (req.address()     != null) farmer.setAddress(req.address());
        if (req.state()       != null) farmer.setState(req.state());
        if (req.district()    != null) farmer.setDistrict(req.district());
        if (req.pincode()     != null) farmer.setPincode(req.pincode());
        if (req.farmAreaAcres() != null) farmer.setFarmAreaAcres(req.farmAreaAcres());
        if (req.primaryCrop() != null) farmer.setPrimaryCrop(req.primaryCrop());
        return toResponse(farmerRepo.save(farmer));
    }

    @Transactional
    public FarmerResponse verify(Long farmerId) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new BusinessException(404, "Farmer not found: " + farmerId));
        farmer.setStatus(FarmerStatus.VERIFIED);
        farmer = farmerRepo.save(farmer);
        eventPublisher.publish(FkTopics.FARMER_VERIFIED, String.valueOf(farmerId),
                new FkBaseEvent(FkTopics.FARMER_VERIFIED, "farmer-service"));
        return toResponse(farmer);
    }

    @Transactional
    public FarmerResponse suspend(Long farmerId) {
        Farmer farmer = farmerRepo.findById(farmerId)
                .orElseThrow(() -> new BusinessException(404, "Farmer not found: " + farmerId));
        farmer.setStatus(FarmerStatus.SUSPENDED);
        return toResponse(farmerRepo.save(farmer));
    }

    private FarmerResponse toResponse(Farmer f) {
        return new FarmerResponse(f.getId(), f.getUserId(), f.getFarmName(),
                f.getAddress(), f.getState(), f.getDistrict(), f.getPincode(),
                f.getFarmAreaAcres(), f.getPrimaryCrop(), f.getStatus(), f.getCreatedAt());
    }
}
