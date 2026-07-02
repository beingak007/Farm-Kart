package com.farmkart.warehouse.repository.config;

import com.farmkart.starter.common.migration.FlywayMigrationDefaults;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * PostgreSQL datasource for warehouse app data (photos, ratings, comments, live location).
 * Core warehouse/booking rows remain on MySQL (primary autoconfig).
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.farmkart.warehouse.app.repository",
        entityManagerFactoryRef = "warehouseAppEntityManagerFactory",
        transactionManagerRef = "warehouseAppTransactionManager")
public class WarehouseAppDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.warehouse-app-datasource")
    public DataSourceProperties warehouseAppDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "warehouseAppDataSource")
    @ConfigurationProperties("spring.warehouse-app-datasource.hikari")
    public DataSource warehouseAppDataSource() {
        return warehouseAppDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "warehouseAppEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean warehouseAppEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("warehouseAppDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.farmkart.warehouse.app.entity")
                .persistenceUnit("warehouse-app")
                .properties(properties)
                .build();
    }

    @Bean(name = "warehouseAppTransactionManager")
    public PlatformTransactionManager warehouseAppTransactionManager(
            @Qualifier("warehouseAppEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(initMethod = "migrate")
    public Flyway warehouseAppFlyway(@Qualifier("warehouseAppDataSource") DataSource dataSource) {
        return FlywayMigrationDefaults.load(dataSource, "classpath:db/migration/warehouse_app");
    }
}
