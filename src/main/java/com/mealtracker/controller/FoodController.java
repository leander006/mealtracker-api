package com.mealtracker.controller;

import com.mealtracker.dto.FoodSearchResponse;
import com.mealtracker.dto.ScanResultResponse;
import com.mealtracker.service.MlServiceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final MlServiceClient mlServiceClient;

    public FoodController(MlServiceClient mlServiceClient) {
        this.mlServiceClient = mlServiceClient;
    }

    /** Scan entry point. Requires auth (unlike search) since a scan result
     *  isn't useful without a logged-in user to eventually save it against. */
    @PostMapping(value = "/scan", consumes = "multipart/form-data")
    public ResponseEntity<ScanResultResponse> scan(
            @RequestParam("photo") MultipartFile photo,
            Authentication authentication
    ) throws IOException {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(mlServiceClient.estimateMeal(photo));
    }

    /** Search entry point - public, so users can browse the food library
     *  before creating an account. */
    @GetMapping("/search")
    public ResponseEntity<FoodSearchResponse> search(@RequestParam String q) {
        return ResponseEntity.ok(mlServiceClient.searchFoodLibrary(q));
    }
}
