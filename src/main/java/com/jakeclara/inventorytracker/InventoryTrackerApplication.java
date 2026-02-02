package com.jakeclara.inventorytracker;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

@SpringBootApplication
public class InventoryTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryTrackerApplication.class, args);
	}

	@Bean
	CommandLineRunner runSql(DataSource dataSource) {
    return args -> {
        ClassPathResource resource = new ClassPathResource("seed.sql");
        ScriptUtils.executeSqlScript(dataSource.getConnection(), resource);
        System.out.println("✅ seed.sql executed");
    };
}


}
