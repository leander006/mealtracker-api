package com.mealtracker.meal_tracker_api.inference.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mealtracker.meal_tracker_api.inference.dto.InferenceDtos.PortionEstimate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeminiPortionClient {

    private final RestClient geminiRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.model:gemini-2.5-flash}")
    private String model;

    /**
     * Same job as the OpenAI/Claude versions before it: given a KNOWN food
     * label, estimate portion size in grams. Gemini's request shape is
     * "inline_data" for images rather than OpenAI's data-URI or Claude's
     * "source" block - this is the only class that changes when swapping
     * providers.
     */
    public PortionEstimate estimatePortion(byte[] imageBytes, String foodLabel) {
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        String prompt = """
                This image shows: %s (already identified).
                Estimate the portion size in grams, based on typical plate/
                reference-object scale. Respond with ONLY a JSON object,
                no other text, in exactly this shape:
                {"estimatedPortionGrams": <integer>}
                """.formatted(foodLabel);

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", prompt),
                                        Map.of(
                                                "inline_data", Map.of(
                                                        "mime_type", "image/jpeg",
                                                        "data", base64Image
                                                )
                                        )
                                )
                        )
                )
        );

        // API key goes as a query param for Gemini's REST API, not a header.
        String rawResponse = geminiRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", apiKey)
                        .build(model))
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return parsePortionFromResponse(rawResponse);
    }

    private PortionEstimate parsePortionFromResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            // Gemini's response shape: { candidates: [ { content: { parts: [ { text: "..." } ] } } ] }
            String textContent = root.path("candidates").get(0)
                    .path("content").path("parts").get(0)
                    .path("text").asText();

            // Gemini sometimes wraps JSON in markdown code fences despite
            // instructions - strip those before parsing if present.
            String cleaned = textContent.replaceAll("```json", "").replaceAll("```", "").trim();

            JsonNode portionJson = objectMapper.readTree(cleaned);
            int grams = portionJson.path("estimatedPortionGrams").asInt();
            return new PortionEstimate(grams);
        } catch (Exception e) {
            return new PortionEstimate(150);
        }
    }
}
