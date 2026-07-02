package com.farmkart.warehouse.rest;

import com.farmkart.warehouse.repository.config.WarehouseAppDataSourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.farmkart.warehouse.rest",
        "com.farmkart.warehouse.service"
})
@EntityScan(basePackages = "com.farmkart.warehouse.repository.entity")
@EnableJpaRepositories(basePackages = "com.farmkart.warehouse.repository")
@Import(WarehouseAppDataSourceConfig.class)
public class WarehouseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(WarehouseServiceApplication.class, args);
    }
}
