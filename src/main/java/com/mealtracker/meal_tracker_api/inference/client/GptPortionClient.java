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
public class GptPortionClient {

    private final RestClient openAiRestClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.model:gpt-5-mini}")
    private String model;

    /**
     * Same job as the Claude version: given a KNOWN food label, estimate
     * portion size in grams. OpenAI's request/response shape differs from
     * Claude's - this is the only class that needed to change when
     * switching providers, everything else in the pipeline is unaffected.
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

        // OpenAI's chat completions format: image goes inside "content" as
        // an image_url object with a data URI, not a separate "source"
        // block like Claude's shape.
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", 150,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", List.of(
                                        Map.of("type", "text", "text", prompt),
                                        Map.of(
                                                "type", "image_url",
                                                "image_url", Map.of(
                                                        "url", "data:image/jpeg;base64," + base64Image
                                                )
                                        )
                                )
                        )
                )
        );

        String rawResponse = openAiRestClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        return parsePortionFromResponse(rawResponse);
    }

    private PortionEstimate parsePortionFromResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            // OpenAI's response shape: { choices: [ { message: { content: "..." } } ] }
            String textContent = root.path("choices").get(0)
                    .path("message").path("content").asText();

            JsonNode portionJson = objectMapper.readTree(textContent);
            int grams = portionJson.path("estimatedPortionGrams").asInt();
            return new PortionEstimate(grams);
        } catch (Exception e) {
            // Same graceful fallback as before - don't fail the whole
            // scan over a parsing hiccup.
            return new PortionEstimate(150);
        }
    }
}
