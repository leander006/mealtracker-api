package com.mealtracker.meal_tracker_api.auth.controller;

import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.AuthResponse;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.GoogleAuthRequest;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.LoginRequest;
import com.mealtracker.meal_tracker_api.auth.dto.AuthDtos.SignupRequest;
import com.mealtracker.meal_tracker_api.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.http.auth.InvalidCredentialsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws InvalidCredentialsException {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        // Deliberately one endpoint for both "Google signup" and "Google
        // login" - unlike password auth, there's no meaningful distinction
        // to the client; the service layer decides create-vs-reuse internally.
        AuthResponse response = authService.googleAuth(request);
        return ResponseEntity.ok(response);
    }
}
