package com.mealtracker.meal_tracker_api.inference.controller;

import com.mealtracker.meal_tracker_api.common.security.AuthenticatedUserId;
import com.mealtracker.meal_tracker_api.inference.dto.InferenceDtos.ScanResponse;
import com.mealtracker.meal_tracker_api.inference.service.InferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/inference")
@RequiredArgsConstructor
public class InferenceController {

    private final InferenceService inferenceService;

    @PostMapping(value = "/scan", consumes = "multipart/form-data")
    public ResponseEntity<ScanResponse> scan(
            @AuthenticatedUserId UUID userId,
            @RequestParam("image") MultipartFile image) throws IOException {

        // NOTE: S3 upload is intentionally not wired in yet - imageUrl is
        // left null here. Add the S3 upload call before this line once
        // you build that piece, then pass the real URL through.
        String imageUrl = null;

        ScanResponse response = inferenceService.scan(
                image.getBytes(), image.getOriginalFilename(), imageUrl);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
