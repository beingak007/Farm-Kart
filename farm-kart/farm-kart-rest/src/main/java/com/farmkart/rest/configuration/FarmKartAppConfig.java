package com.farmkart.rest.configuration;

import com.farmkart.common.rest.configuration.CommonRestConfig;
import com.farmkart.framework.client.annotations.EnableFarmKartFramework;
import com.farmkart.framework.repository.config.FrameworkDataSourceConfig;
import com.farmkart.framework.service.config.FrameworkServiceConfig;
import com.farmkart.starter.common.migration.FlywayMigrationDefaults;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

@Configuration
@Import({CommonRestConfig.class, FrameworkDataSourceConfig.class, FrameworkServiceConfig.class})
@ComponentScan(basePackages = {"com.farmkart.rest", "com.farmkart.service"})
@EnableFarmKartFramework
public class FarmKartAppConfig {

    @Bean(initMethod = "migrate")
    public Flyway marketplaceFlyway(DataSource dataSource) {
        return FlywayMigrationDefaults.load(dataSource, "classpath:db/migration/marketplace");
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
