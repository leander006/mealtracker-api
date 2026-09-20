package com.mealtracker.controller;

import com.mealtracker.dto.GoalRequest;
import com.mealtracker.dto.GoalResponse;
import com.mealtracker.service.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @GetMapping
    public ResponseEntity<GoalResponse> getGoals(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return goalService.getGoals(userId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PutMapping
    public ResponseEntity<GoalResponse> saveGoals(@Valid @RequestBody GoalRequest request, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(goalService.saveGoals(userId, request));
    }
}
