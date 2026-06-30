package com.farmkart.market_price.service;

import com.farmkart.market_price.client.dto.MandiPriceResponse;
import com.farmkart.market_price.repository.MandiPriceRepository;
import com.farmkart.market_price.repository.entity.MandiPrice;
import com.farmkart.starter.common.events.DomainEventPublisher;
import com.farmkart.starter.common.events.FkBaseEvent;
import com.farmkart.starter.common.events.FkTopics;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MandiPriceService {

    private final MandiPriceRepository priceRepo;
    private final DomainEventPublisher eventPublisher;

    public MandiPriceService(MandiPriceRepository priceRepo, DomainEventPublisher eventPublisher) {
        this.priceRepo = priceRepo;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<MandiPriceResponse> getLatestByCropAndState(String cropName, String state) {
        return priceRepo.findLatestByCropAndState(cropName, state).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<MandiPriceResponse> getPriceHistory(String cropName, String state, LocalDate from, LocalDate to) {
        return priceRepo.findByCropNameAndStateAndPriceDateBetweenOrderByPriceDateAsc(cropName, state, from, to)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public MandiPriceResponse ingestPrice(MandiPrice price) {
        MandiPrice saved = priceRepo.save(price);
        eventPublisher.publish(FkTopics.PRICE_UPDATED, saved.getCropName(),
                new FkBaseEvent(FkTopics.PRICE_UPDATED, "market-price-service"));
        return toResponse(saved);
    }

    private MandiPriceResponse toResponse(MandiPrice mp) {
        return new MandiPriceResponse(mp.getId(), mp.getCropName(), mp.getMandiName(),
                mp.getState(), mp.getDistrict(), mp.getPricePerQuintal(),
                mp.getMinPrice(), mp.getMaxPrice(), mp.getPriceDate(), mp.getSource());
    }
}
