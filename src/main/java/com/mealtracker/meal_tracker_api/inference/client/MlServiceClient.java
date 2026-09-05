package com.mealtracker.meal_tracker_api.inference.client;

import com.mealtracker.meal_tracker_api.inference.dto.InferenceDtos.ClassificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MlServiceClient {

    private final RestClient mlServiceRestClient;

    public ClassificationResult classify(byte[] imageBytes, String filename) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        // IMPORTANT: do NOT manually set .contentType(MULTIPART_FORM_DATA)
        // here. When the body is a MultiValueMap containing a Resource,
        // Spring's FormHttpMessageConverter automatically detects it and
        // builds a correct multipart/form-data request WITH a generated
        // boundary parameter. Setting the content type manually short-
        // circuits that auto-generation, producing a boundary-less,
        // unparseable multipart body - which is exactly what was causing
        // FastAPI to report "image" as missing despite it being sent.
        return mlServiceRestClient.post()
                .uri("/predict")
                .body(body)
                .retrieve()
                .body(ClassificationResult.class);
    }
}