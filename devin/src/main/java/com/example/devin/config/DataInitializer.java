package com.example.devin.config;

import com.example.devin.mapper.UserMapper;
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
    private UserMapper userMapper;

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
                
                // Encode password directly here instead of in userService.register
                String rawPassword = "admin123";
                String encodedPassword = passwordEncoder.encode(rawPassword);
                System.out.println("Admin password: " + rawPassword);
                System.out.println("Encoded admin password length: " + encodedPassword.length());
                adminUser.setPassword(encodedPassword);
                
                // Set role explicitly to admin
                adminUser.setRole("admin");
                
                // Insert directly using mapper to bypass userService.register which might override the role
                userMapper.insert(adminUser);
                System.out.println("Admin user created successfully with role: " + adminUser.getRole());
                
                // Verify the user was created correctly
                User createdAdmin = userService.findByUsername("admin");
                if (createdAdmin != null) {
                    System.out.println("Verified admin user exists with role: " + createdAdmin.getRole());
                    System.out.println("Admin password hash length: " + createdAdmin.getPassword().length());
                }
            } else {
                System.out.println("Admin user already exists");
                User existingAdmin = userService.findByUsername("admin");
                System.out.println("Existing admin role: " + existingAdmin.getRole());
                System.out.println("Existing admin password hash length: " + existingAdmin.getPassword().length());
            }
        };
    }
}
