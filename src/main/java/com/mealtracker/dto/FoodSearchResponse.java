package com.mealtracker.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;
import java.util.Map;

/** Mirrors the ml-service's GET /food-library/search response shape.
 *
 *  Unlike ScanResultResponse, this one is asymmetric: the ml-service sends
 *  snake_case (food_id, per_100g), but the frontend (Search.jsx) reads this
 *  same response back as camelCase (food.foodId, food.per100g). @JsonAlias
 *  only affects deserialization, so it lets Jackson accept the ml-service's
 *  snake_case keys on the way in while still serializing back out to the
 *  browser using the record's own camelCase names, matching both contracts
 *  without changing either side. per100g needs the alias explicitly (rather
 *  than a blanket naming strategy) since "100g" has no case boundary for an
 *  automatic camelCase<->snake_case converter to split on. */
public record FoodSearchResponse(List<FoodResult> results) {
    public record FoodResult(
        @JsonAlias("food_id") Long foodId,
        String description,
        String source,
        boolean verified,
        @JsonAlias("per_100g") Map<String, Double> per100g
    ) {}
}
