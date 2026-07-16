package com.farmkart.rest.configuration;

import com.farmkart.common.rest.configuration.CommonRestConfig;
import com.farmkart.framework.client.annotations.EnableFarmKartFramework;
import com.farmkart.framework.repository.config.FrameworkDataSourceConfig;
import com.farmkart.framework.service.config.FrameworkServiceConfig;
import com.farmkart.starter.common.events.FkKafkaConsumerConfiguration;
import com.farmkart.starter.common.migration.FlywayMigrationDefaults;
import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

/**
 * Single Farm Kart application: marketplace plus all merged domain modules
 * (farmer, buyer, logistics, warehouse, market-price, admin, product-catalog,
 * reporting, ai-advisory). Notification and agent remain separate services.
 */
@Configuration
@Import({CommonRestConfig.class, FrameworkDataSourceConfig.class, FrameworkServiceConfig.class,
        FkKafkaConsumerConfiguration.class})
@ComponentScan(basePackages = {
        "com.farmkart.rest",
        "com.farmkart.service",
        "com.farmkart.farmer",
        "com.farmkart.buyer",
        "com.farmkart.logistics",
        "com.farmkart.warehouse",
        "com.farmkart.market_price",
        "com.farmkart.admin",
        "com.farmkart.product_catalog",
        "com.farmkart.reporting",
        "com.farmkart.ai_advisory"
})
// Domain repositories/entities on the primary MySQL datasource.
// (com.farmkart.repository is registered by CommonRestConfig; the warehouse
// Postgres side lives in com.farmkart.warehouse.app.* via WarehouseAppDataSourceConfig.)
@EnableJpaRepositories(basePackages = {
        "com.farmkart.farmer.repository",
        "com.farmkart.buyer.repository",
        "com.farmkart.logistics.repository",
        "com.farmkart.warehouse.repository",
        "com.farmkart.market_price.repository",
        "com.farmkart.admin.repository",
        "com.farmkart.product_catalog.repository",
        "com.farmkart.reporting.repository",
        "com.farmkart.ai_advisory.repository"
})
@EntityScan(basePackages = {
        "com.farmkart.farmer.repository.entity",
        "com.farmkart.buyer.repository.entity",
        "com.farmkart.logistics.repository.entity",
        "com.farmkart.warehouse.repository.entity",
        "com.farmkart.market_price.repository.entity",
        "com.farmkart.admin.repository.entity",
        "com.farmkart.product_catalog.repository.entity",
        "com.farmkart.reporting.repository.entity",
        "com.farmkart.ai_advisory.repository.entity"
})
@EnableFarmKartFramework
@EnableAsync
public class FarmKartAppConfig {

    /** All domain migrations run against the single MySQL database. */
    @Bean(initMethod = "migrate")
    public Flyway marketplaceFlyway(DataSource dataSource) {
        return FlywayMigrationDefaults.load(dataSource,
                "classpath:db/migration/marketplace",
                "classpath:db/migration/farmer",
                "classpath:db/migration/buyer",
                "classpath:db/migration/logistics",
                "classpath:db/migration/warehouse",
                "classpath:db/migration/market_price",
                "classpath:db/migration/admin",
                "classpath:db/migration/product_catalog",
                "classpath:db/migration/reporting",
                "classpath:db/migration/ai_advisory");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
