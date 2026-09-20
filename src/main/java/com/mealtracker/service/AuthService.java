package com.mealtracker.service;

import com.mealtracker.dto.AuthResponse;
import com.mealtracker.dto.LoginRequest;
import com.mealtracker.dto.RegisterRequest;
import com.mealtracker.entity.User;
import com.mealtracker.repository.UserRepository;
import com.mealtracker.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getDisplayName());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId(), user.getDisplayName());
    }
}
