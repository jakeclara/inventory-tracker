package com.jakeclara.inventorytracker.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@Configuration
@Profile("development")
public class DevelopmentDataConfig {

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentDataConfig.class);
    @Bean
	CommandLineRunner runSql(DataSource dataSource) {
        return args -> {
            try {
                ClassPathResource resource = new ClassPathResource("seed.sql");
                ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
                logger.info("✅ Database seeded successfully from seed.sql");
            } catch (Exception e) {
                logger.error("Error executing seed.sql", e);
            }
        };
    }
}
