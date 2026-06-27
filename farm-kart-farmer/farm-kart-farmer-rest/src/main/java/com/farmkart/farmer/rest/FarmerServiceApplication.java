package com.farmkart.farmer.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.farmkart.farmer")
public class FarmerServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmerServiceApplication.class, args);
    }
}
