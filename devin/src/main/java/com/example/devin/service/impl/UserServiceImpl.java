package com.example.devin.service.impl;

import com.example.devin.mapper.UserMapper;
import com.example.devin.model.User;
import com.example.devin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

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
        System.out.println("Registering user: " + user.getUsername() + " with role: " + (user.getRole() != null ? user.getRole() : "null"));
        
        // Encode password before saving
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        System.out.println("Original password length: " + user.getPassword().length() + ", Encoded password length: " + encodedPassword.length());
        user.setPassword(encodedPassword);
        
        // Preserve the role if it's already set
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
            System.out.println("Setting default role: user");
        } else {
            System.out.println("Preserving existing role: " + user.getRole());
        }
        
        userMapper.insert(user);
        System.out.println("User registered successfully: " + user.getUsername() + " with role: " + user.getRole());
        return user;
    }
}
