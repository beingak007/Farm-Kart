package com.farmkart.buyer.service;

import com.farmkart.buyer.constants.BuyerServiceConstants;
import com.farmkart.buyer.client.dto.BuyerOnboardRequest;
import com.farmkart.buyer.client.dto.BuyerResponse;
import com.farmkart.buyer.repository.BuyerRepository;
import com.farmkart.buyer.repository.entity.Buyer;
import com.farmkart.starter.common.events.BuyerCreatedEvent;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import com.farmkart.starter.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class BuyerService {

    private final BuyerRepository buyerRepo;
    private final DomainEventPublisher eventPublisher;

    public BuyerService(BuyerRepository buyerRepo, DomainEventPublisher eventPublisher) {
        this.buyerRepo = buyerRepo;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public BuyerResponse onboard(BuyerOnboardRequest req) {
        if (buyerRepo.existsByUserId(req.userId())) {
            throw new BusinessException(409, "Buyer profile already exists for userId: " + req.userId());
        }
        Buyer buyer = new Buyer();
        buyer.setUserId(req.userId());
        buyer.setDisplayName(req.displayName());
        buyer.setAddress(req.address());
        buyer.setState(req.state());
        buyer.setPincode(req.pincode());
        buyer.setGstin(req.gstin());
        buyer.setCompanyName(req.companyName());
        if (req.buyerType() != null) buyer.setBuyerType(req.buyerType());
        buyer = buyerRepo.save(buyer);

        BuyerCreatedEvent event = new BuyerCreatedEvent(
                new FkBaseEvent(FkTopics.BUYER_CREATED, BuyerServiceConstants.SERVICE_NAME),
                buyer.getId(), buyer.getUserId(), buyer.getDisplayName(),
                buyer.getState(), Instant.now());
        eventPublisher.publish(FkTopics.BUYER_CREATED, String.valueOf(buyer.getId()), event);
        return toResponse(buyer);
    }

    @Transactional(readOnly = true)
    public BuyerResponse getByUserId(Long userId) {
        return toResponse(buyerRepo.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(404, "Buyer not found for userId: " + userId)));
    }

    @Transactional(readOnly = true)
    public BuyerResponse getById(Long buyerId) {
        return toResponse(buyerRepo.findById(buyerId)
                .orElseThrow(() -> new BusinessException(404, "Buyer not found: " + buyerId)));
    }

    private BuyerResponse toResponse(Buyer b) {
        return new BuyerResponse(b.getId(), b.getUserId(), b.getDisplayName(),
                b.getAddress(), b.getState(), b.getPincode(),
                b.getGstin(), b.getCompanyName(), b.getBuyerType(), b.getStatus(), b.getCreatedAt());
    }
}
