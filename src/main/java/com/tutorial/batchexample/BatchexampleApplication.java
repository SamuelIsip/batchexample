package com.tutorial.batchexample;

import com.tutorial.batchexample.configuration.BatchConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class BatchexampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BatchexampleApplication.class, args);
	}

}
