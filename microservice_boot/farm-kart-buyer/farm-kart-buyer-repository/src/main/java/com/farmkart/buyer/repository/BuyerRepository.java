package com.farmkart.buyer.repository;

import com.farmkart.buyer.repository.entity.Buyer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BuyerRepository extends JpaRepository<Buyer, Long> {
    Optional<Buyer> findByUserId(Long userId);
    boolean existsByUserId(Long userId);
}
