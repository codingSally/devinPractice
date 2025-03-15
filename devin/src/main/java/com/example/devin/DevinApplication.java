package com.example.devin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.logging.Logger;

@SpringBootApplication
public class DevinApplication {
	private static final Logger logger = Logger.getLogger(DevinApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(DevinApplication.class, args);
		logger.info("Application started successfully");
	}

}
