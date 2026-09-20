package com.mealtracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** What the client sends after the user confirms/adjusts a scan result,
 *  picks a search result, or manually enters a food. quantityGrams is
 *  whatever the user confirmed - NOT necessarily the raw model estimate. */
public record LogMealRequest(
    @NotBlank String foodDescription,
    @NotBlank String source,   // "scan" | "search" | "manual"
    Long mlFoodId,
    @NotNull @Positive Double quantityGrams,
    @NotNull Double caloriesKcal,
    Double proteinG,
    Double carbsG,
    Double fatG,
    Double fiberG,
    Double sugarG,
    Double sodiumMg,
    Double detectionConfidence,
    String imageUrl
) {}
