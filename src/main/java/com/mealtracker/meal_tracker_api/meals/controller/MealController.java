package com.mealtracker.meal_tracker_api.meals.controller;

import com.mealtracker.meal_tracker_api.common.security.AuthenticatedUserId;
import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.LogMealRequest;
import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.MealResponse;
import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.WeeklySummaryResponse;
import com.mealtracker.meal_tracker_api.meals.service.MealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {

    private final MealService mealService;

    @PostMapping
    public ResponseEntity<MealResponse> logMeal(
            @AuthenticatedUserId UUID userId,
            @Valid @RequestBody LogMealRequest request) {
        MealResponse response = mealService.logMeal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<MealResponse>> getMeals(
            @AuthenticatedUserId UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(mealService.getMeals(userId, from, to));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<MealResponse>> getRecentMeals(@AuthenticatedUserId UUID userId) {
        return ResponseEntity.ok(mealService.getRecentMeals(userId));
    }

    @GetMapping("/summary")
    public ResponseEntity<WeeklySummaryResponse> getWeeklySummary(@AuthenticatedUserId UUID userId) {
        return ResponseEntity.ok(mealService.getWeeklySummary(userId));
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(
            @AuthenticatedUserId UUID userId,
            @PathVariable UUID mealId) {
        mealService.deleteMeal(userId, mealId);
        return ResponseEntity.noContent().build();
    }
}
