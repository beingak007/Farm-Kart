package com.farmkart.market_price.repository;

import com.farmkart.market_price.repository.entity.MandiPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MandiPriceRepository extends JpaRepository<MandiPrice, Long> {

    Page<MandiPrice> findByCropNameAndPriceDateOrderByPriceDateDesc(String cropName, LocalDate date, Pageable pageable);

    List<MandiPrice> findByCropNameAndStateAndPriceDateBetweenOrderByPriceDateAsc(
            String cropName, String state, LocalDate from, LocalDate to);

    @Query("SELECT mp FROM MandiPrice mp WHERE mp.cropName = :crop AND mp.priceDate = " +
           "(SELECT MAX(mp2.priceDate) FROM MandiPrice mp2 WHERE mp2.cropName = :crop AND mp2.state = :state)")
    List<MandiPrice> findLatestByCropAndState(@Param("crop") String cropName, @Param("state") String state);
}
