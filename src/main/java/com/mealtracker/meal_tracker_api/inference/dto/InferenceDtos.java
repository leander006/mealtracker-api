package com.mealtracker.meal_tracker_api.inference.dto;

public class InferenceDtos {

    // What ml-service returns
    public record ClassificationResult(String foodLabel, double confidence) {}

    // What Claude gives us back for portion estimation
    public record PortionEstimate(int estimatedPortionGrams) {}

    // What USDA gives us back - macros PER the estimated portion, already scaled
    public record MacroLookupResult(
            double calories,
            double proteinG,
            double carbsG,
            double fatG,
            double fiberG
    ) {}

    // The final combined response the frontend actually receives
    public record ScanResponse(
            String foodLabel,
            double confidence,
            int estimatedPortionGrams,
            MacroLookupResult macros,
            String imageUrl
    ) {}
}
