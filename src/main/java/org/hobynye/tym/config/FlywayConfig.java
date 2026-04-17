package org.hobynye.tym.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Manual Flyway configuration required because Spring Boot 4.0 removed Flyway
 * auto-configuration. Runs migrations on startup unless spring.flyway.enabled=false
 * (set in the 'test' profile to keep unit tests on H2 with ddl-auto=create-drop).
 */
@Configuration
public class FlywayConfig {

    @Bean
    @ConditionalOnProperty(name = "spring.flyway.enabled", matchIfMissing = true)
    public Flyway flyway(DataSource dataSource,
                         @Value("${spring.flyway.locations:classpath:db/migration}") String locations) {
        Flyway flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations(locations)
                .load();
        flyway.migrate();
        return flyway;
    }
}
