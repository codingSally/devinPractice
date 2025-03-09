package com.example.devin.config;

import com.example.devin.model.User;
import com.example.devin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Check if admin user exists
            if (userService.findByUsername("admin") == null) {
                System.out.println("Creating admin user...");
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setRole("admin");
                userService.register(adminUser);
                System.out.println("Admin user created successfully");
            } else {
                System.out.println("Admin user already exists");
            }
        };
    }
}
