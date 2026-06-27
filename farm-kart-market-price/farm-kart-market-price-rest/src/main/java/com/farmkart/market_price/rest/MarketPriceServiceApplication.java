package com.farmkart.market_price.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.farmkart.market_price")
public class MarketPriceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MarketPriceServiceApplication.class, args);
    }
}
