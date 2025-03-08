package com.example.devin.service;

import com.example.devin.model.User;

public interface UserService {
    User findByUsername(String username);
    User register(User user);
}
