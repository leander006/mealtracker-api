package com.mealtracker.meal_tracker_api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AuthDtos {

    public record SignupRequest(
            @Email(message = "Must be a valid email address")
            @NotBlank(message = "Email is required")
            String email,

            @NotBlank(message = "Password is required")
            @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters")
            String password,

            @NotBlank(message = "Name is required")
            @Size(max = 100)
            String name
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    /**
     * The frontend sends only the raw ID token it got from Google's SDK -
     * nothing else. We never trust a client-supplied email/name for Google
     * auth; everything comes from the verified token payload itself, since
     * a client could otherwise claim to be any email it wants.
     */
    public record GoogleAuthRequest(
            @NotBlank(message = "Google ID token is required")
            String idToken
    ) {}

    public record AuthResponse(
            UUID userId,
            String name,
            String token
    ) {}
}
