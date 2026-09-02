package com.mealtracker.meal_tracker_api.inference.client;

import tools.jackson.databind.JsonNode;
import com.mealtracker.meal_tracker_api.inference.dto.InferenceDtos.MacroLookupResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
@RequiredArgsConstructor
public class UsdaMacroClient {

    private final RestClient usdaRestClient;

    @Value("${usda.api.key}")
    private String apiKey;

    /**
     * Looks up per-100g macros for the given food label, then scales them
     * to the actual estimated portion. USDA's search isn't a perfect
     * exact-match system, so we take the first (most relevant) result -
     * fine for a portfolio project, but worth knowing this is a
     * simplification versus a production nutrition app's matching logic.
     */
    public MacroLookupResult lookupMacros(String foodLabel, int portionGrams) {
        JsonNode searchResult = usdaRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/fdc/v1/foods/search")
                        .queryParam("query", foodLabel.replace("_", " "))
                        .queryParam("pageSize", 1)
                        .queryParam("api_key", apiKey)
                        .build())
                .retrieve()
                .body(JsonNode.class);

        JsonNode foods = searchResult.path("foods");
        if (!foods.isArray() || foods.isEmpty()) {
            // No match found - return zeros rather than crash the whole
            // scan. Frontend can show "macros unavailable, please add
            // manually" for this edge case.
            return new MacroLookupResult(0, 0, 0, 0, 0);
        }

        JsonNode nutrients = foods.get(0).path("foodNutrients");
        double per100gCalories = extractNutrient(nutrients, "Energy");
        double per100gProtein = extractNutrient(nutrients, "Protein");
        double per100gCarbs = extractNutrient(nutrients, "Carbohydrate, by difference");
        double per100gFat = extractNutrient(nutrients, "Total lipid (fat)");
        double per100gFiber = extractNutrient(nutrients, "Fiber, total dietary");

        double scale = portionGrams / 100.0;
        return new MacroLookupResult(
                round(per100gCalories * scale),
                round(per100gProtein * scale),
                round(per100gCarbs * scale),
                round(per100gFat * scale),
                round(per100gFiber * scale)
        );
    }

    private double extractNutrient(JsonNode nutrients, String nutrientName) {
        for (JsonNode nutrient : nutrients) {
            if (nutrientName.equalsIgnoreCase(nutrient.path("nutrientName").asText())) {
                return nutrient.path("value").asDouble(0.0);
            }
        }
        return 0.0;
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
