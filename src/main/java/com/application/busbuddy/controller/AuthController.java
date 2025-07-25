package com.application.busbuddy.controller;

import com.application.busbuddy.dto.request.LoginRequest;
import com.application.busbuddy.dto.response.LoginResponse;
import com.application.busbuddy.model.User;
import com.application.busbuddy.model.Provider;
import com.application.busbuddy.repository.UserRepository;
import com.application.busbuddy.repository.ProviderRepository;
import com.application.busbuddy.config.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        // Check User first
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                String token = jwtUtil.generateToken(user.getEmail());
                return ResponseEntity.ok(new LoginResponse(token, user.getRole().toString()));
            }
        }

        // Check Provider next
        Optional<Provider> providerOpt = providerRepository.findByEmail(request.getEmail());
        if (providerOpt.isPresent()) {
            Provider provider = providerOpt.get();
            if (passwordEncoder.matches(request.getPassword(), provider.getPassword())) {
                String token = jwtUtil.generateToken(provider.getEmail());
                return ResponseEntity.ok(new LoginResponse(token, "PROVIDER"));
            }
        }

        // If no match found
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}
