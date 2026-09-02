package com.mealtracker.meal_tracker_api.inference.client;

import com.mealtracker.meal_tracker_api.inference.dto.InferenceDtos.ClassificationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.core.io.ByteArrayResource;

@Component
@RequiredArgsConstructor
public class MlServiceClient {

    private final RestClient mlServiceRestClient;

    public ClassificationResult classify(byte[] imageBytes, String filename) {
        // ml-service expects multipart/form-data with a field named "image" -
        // matching exactly what FastAPI's UploadFile(File(...)) expects.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return filename;
            }
        });

        return mlServiceRestClient.post()
                .uri("/predict")
                .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(ClassificationResult.class);
    }
}
