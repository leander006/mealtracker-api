package com.mealtracker.service;

import com.mealtracker.dto.FoodSearchResponse;
import com.mealtracker.dto.ScanResultResponse;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/** Everything the backend needs from the Python ml-service, in one place.
 *  If the ml-service moves or gets split up later, this is the only class
 *  that needs to change. */
@Service
public class MlServiceClient {

    private final WebClient webClient;

    public MlServiceClient(WebClient mlServiceWebClient) {
        this.webClient = mlServiceWebClient;
    }

    public ScanResultResponse estimateMeal(MultipartFile photo) throws IOException {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("photo", photo.getResource())
                .filename(photo.getOriginalFilename() != null ? photo.getOriginalFilename() : "photo.jpg");

        return webClient.post()
                .uri("/estimate-meal")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .retrieve()
                .bodyToMono(ScanResultResponse.class)
                .block();
    }

    public FoodSearchResponse searchFoodLibrary(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/food-library/search").queryParam("q", query).build())
                .retrieve()
                .bodyToMono(FoodSearchResponse.class)
                .block();
    }
}
