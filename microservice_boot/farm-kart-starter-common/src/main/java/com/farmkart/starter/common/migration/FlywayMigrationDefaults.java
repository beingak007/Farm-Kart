package com.farmkart.starter.common.migration;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

/**
 * Shared Flyway settings aligned with Yagna microservices_boot conventions:
 * epoch-based versions, baseline-on-migrate, out-of-order merges.
 */
public final class FlywayMigrationDefaults {

    private FlywayMigrationDefaults() {
    }

    public static Flyway load(DataSource dataSource, String... locations) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .validateOnMigrate(false)
                .load();
    }
}
