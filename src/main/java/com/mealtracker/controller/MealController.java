package com.mealtracker.controller;

import com.mealtracker.dto.LogMealRequest;
import com.mealtracker.entity.MealEntry;
import com.mealtracker.service.MealService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<MealEntry> logMeal(@Valid @RequestBody LogMealRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(mealService.logMeal(userId, request));
    }

    @GetMapping("/today")
    public ResponseEntity<List<MealEntry>> today(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(mealService.getToday(userId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MealEntry>> history(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(mealService.getHistory(userId));
    }
}
