package com.example.aggregator;

import com.netflix.config.ConfigurationManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class AggregatorApplication {

	public static void main(String[] args) throws IOException {
		ConfigurationManager.loadPropertiesFromResources("application.properties");
		SpringApplication.run(AggregatorApplication.class, args);
	}
}
