package com.example.devin.config;

import com.example.devin.mapper.UserMapper;
import com.example.devin.model.User;
import com.example.devin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.logging.Logger;

@Configuration
public class DataInitializer {
    private static final Logger logger = Logger.getLogger(DataInitializer.class.getName());

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Check if admin user exists
            if (userService.findByUsername("admin") == null) {
                logger.info("Creating admin user...");
                User adminUser = new User();
                adminUser.setUsername("admin");
                
                // Encode password directly here instead of in userService.register
                String rawPassword = "admin123";
                String encodedPassword = passwordEncoder.encode(rawPassword);
                logger.info("Admin password: " + rawPassword);
                logger.info("Encoded admin password length: " + encodedPassword.length());
                adminUser.setPassword(encodedPassword);
                
                // Set role explicitly to admin
                adminUser.setRole("admin");
                
                // Insert directly using mapper to bypass userService.register which might override the role
                userMapper.insert(adminUser);
                logger.info("Admin user created successfully with role: " + adminUser.getRole());
                
                // Verify the user was created correctly
                User createdAdmin = userService.findByUsername("admin");
                if (createdAdmin != null) {
                    logger.info("Verified admin user exists with role: " + createdAdmin.getRole());
                    logger.info("Admin password hash length: " + createdAdmin.getPassword().length());
                }
            } else {
                logger.info("Admin user already exists");
                User existingAdmin = userService.findByUsername("admin");
                logger.info("Existing admin role: " + existingAdmin.getRole());
                logger.info("Existing admin password hash length: " + existingAdmin.getPassword().length());
            }
        };
    }
}
