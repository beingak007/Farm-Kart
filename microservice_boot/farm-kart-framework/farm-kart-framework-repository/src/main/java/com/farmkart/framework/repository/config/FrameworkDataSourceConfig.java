package com.farmkart.framework.repository.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import com.farmkart.starter.common.migration.FlywayMigrationDefaults;
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

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.farmkart.framework.repository",
        entityManagerFactoryRef = "frameworkEntityManagerFactory",
        transactionManagerRef = "frameworkTransactionManager")
public class FrameworkDataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.framework-datasource")
    public DataSourceProperties frameworkDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "frameworkDataSource")
    @ConfigurationProperties("spring.framework-datasource.hikari")
    public DataSource frameworkDataSource() {
        return frameworkDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean(name = "frameworkEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean frameworkEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("frameworkDataSource") DataSource dataSource) {

        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");

        return builder
                .dataSource(dataSource)
                .packages("com.farmkart.framework.repository.entity")
                .persistenceUnit("framework")
                .properties(properties)
                .build();
    }

    @Bean(name = "frameworkTransactionManager")
    public PlatformTransactionManager frameworkTransactionManager(
            @Qualifier("frameworkEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean(initMethod = "migrate")
    public Flyway frameworkFlyway(@Qualifier("frameworkDataSource") DataSource dataSource) {
        return FlywayMigrationDefaults.load(dataSource, "classpath:db/migration/framework");
    }
}
