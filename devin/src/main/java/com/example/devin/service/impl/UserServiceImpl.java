package com.example.devin.service.impl;

import com.example.devin.mapper.UserMapper;
import com.example.devin.model.User;
import com.example.devin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.logging.Logger;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getName());

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public User register(User user) {
        logger.info("Registering user: " + user.getUsername() + " with role: " + (user.getRole() != null ? user.getRole() : "null"));
        
        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        logger.info("Original password length: " + user.getPassword().length() + ", Encoded password length: " + encodedPassword.length());
        user.setPassword(encodedPassword);
        
        // Preserve the role if it's already set
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
            logger.info("Setting default role: user");
        } else {
            logger.info("Preserving existing role: " + user.getRole());
        }
        
        userMapper.insert(user);
        logger.info("User registered successfully: " + user.getUsername() + " with role: " + user.getRole());
        return user;
    }
}
