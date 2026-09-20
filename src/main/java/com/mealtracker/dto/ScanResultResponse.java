package com.mealtracker.dto;

import java.util.List;
import java.util.Map;

/** Mirrors the ml-service's POST /estimate-meal response shape. */
public record ScanResultResponse(
    List<ScanItem> items,
    Map<String, Double> mealTotals,
    boolean referenceObjectFound
) {
    public record ScanItem(
        String label,
        String matchedFood,
        String matchType,
        Double detectionConfidence,
        Portion portion,
        Map<String, Double> macros,
        boolean nutritionFound
    ) {}

    public record Portion(Double weightG, Double confidence, String method) {}
}
