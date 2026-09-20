package com.mealtracker.dto;

public record AuthResponse(String token, Long userId, String displayName) {}
