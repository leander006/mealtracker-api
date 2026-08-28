package com.mealtracker.meal_tracker_api.auth.service;

public class InvalidGoogleTokenException extends RuntimeException {
    public InvalidGoogleTokenException() {
        super("Google authentication failed - invalid or expired token");
    }
}
