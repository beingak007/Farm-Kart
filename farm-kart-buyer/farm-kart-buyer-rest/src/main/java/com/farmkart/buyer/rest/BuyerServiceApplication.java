package com.farmkart.buyer.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.farmkart.buyer")
public class BuyerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BuyerServiceApplication.class, args);
    }
}
