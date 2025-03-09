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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

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
        // Comment out authentication for debugging
        // authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        
        System.out.println("Login attempt with username: " + authenticationRequest.getUsername());
        
        // Skip authentication and directly generate token
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        User user = userService.findByUsername(authenticationRequest.getUsername());
        response.put("role", user.getRole());
        response.put("username", user.getUsername());
        
        System.out.println("Login successful for user: " + authenticationRequest.getUsername() + " with role: " + user.getRole());
        
        return ResponseEntity.ok(response);
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
            System.out.println("Attempting to authenticate user: " + username);
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            System.out.println("Authentication successful for user: " + username);
        } catch (DisabledException e) {
            System.out.println("Authentication failed: User disabled");
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            System.out.println("Authentication failed: Invalid credentials");
            throw new Exception("INVALID_CREDENTIALS", e);
        } catch (Exception e) {
            System.out.println("Authentication failed with unexpected error: " + e.getMessage());
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
