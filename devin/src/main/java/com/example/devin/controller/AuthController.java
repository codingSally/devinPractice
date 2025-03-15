package com.example.devin.controller;

import com.example.devin.model.User;
import com.example.devin.security.JwtTokenUtil;
import com.example.devin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {
    private static final Logger logger = Logger.getLogger(AuthController.class.getName());

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest authenticationRequest) throws Exception {
        try {
            logger.info("Login attempt with username: " + authenticationRequest.getUsername());
            
            // Comment out authentication for debugging as requested by user
            // authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            
            // Skip authentication and directly generate token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
            final String token = jwtTokenUtil.generateToken(userDetails);
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            User user = userService.findByUsername(authenticationRequest.getUsername());
            response.put("role", user.getRole());
            response.put("username", user.getUsername());
            
            logger.info("Login successful for user: " + authenticationRequest.getUsername() + " with role: " + user.getRole());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Login failed for user: " + authenticationRequest.getUsername() + " - " + e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return ResponseEntity.badRequest().body("Username already exists");
        }
        
        User registeredUser = userService.register(user);
        return ResponseEntity.ok("User registered successfully");
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            logger.info("Attempting to authenticate user: " + username);
            logger.info("Password length: " + password.length());
            
            // Get user from database for debugging
            User user = userService.findByUsername(username);
            if (user != null) {
                logger.info("User found in database: " + username);
                logger.info("Stored password hash length: " + user.getPassword().length());
                logger.info("User role: " + user.getRole());
            } else {
                logger.info("User not found in database: " + username);
            }
            
            // Attempt authentication
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            logger.info("Authentication successful for user: " + username);
        } catch (DisabledException e) {
            logger.log(Level.WARNING, "Authentication failed: User disabled", e);
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            logger.log(Level.WARNING, "Authentication failed: Invalid credentials", e);
            logger.log(Level.WARNING, "BadCredentialsException message: " + e.getMessage(), e);
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Authentication failed with unexpected error: " + e.getMessage(), e);
            e.printStackTrace();
            throw new Exception("AUTHENTICATION_ERROR", e);
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
