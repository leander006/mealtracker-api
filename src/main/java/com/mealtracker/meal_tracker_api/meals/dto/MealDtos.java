package com.mealtracker.meal_tracker_api.meals.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.util.UUID;

public class MealDtos {

    public record MacrosDto(
            @PositiveOrZero double calories,
            @PositiveOrZero double proteinG,
            @PositiveOrZero double carbsG,
            @PositiveOrZero double fatG,
            @PositiveOrZero double fiberG
    ) {}

    public record LogMealRequest(
            @NotBlank(message = "Food label is required")
            String foodLabel,

            @Min(value = 1, message = "Portion must be a positive number of grams")
            Integer portionGrams,

            @NotNull(message = "Macros are required")
            @Valid
            MacrosDto macros,

            String imageUrl,

            // Optional - defaults to now if the client doesn't send it
            // (e.g. logging a meal slightly after eating it)
            Instant loggedAt
    ) {}

    public record MealResponse(
            UUID mealId,
            String foodLabel,
            Integer portionGrams,
            MacrosDto macros,
            String imageUrl,
            Instant loggedAt
    ) {}

    public record WeeklySummaryResponse(
            double avgCaloriesPerDay,
            double avgProteinG,
            double avgCarbsG,
            double avgFatG,
            double avgFiberG,
            int mealsLogged
    ) {}
}
