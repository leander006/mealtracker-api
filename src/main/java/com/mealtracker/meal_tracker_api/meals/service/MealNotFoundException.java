package com.mealtracker.meal_tracker_api.meals.service;

public class MealNotFoundException extends RuntimeException {
    public MealNotFoundException() {
        // Same message whether the meal doesn't exist at all, or exists
        // but belongs to a different user - never reveal which, since
        // that would let one user probe for the existence of another
        // user's meal IDs.
        super("Meal not found");
    }
}
