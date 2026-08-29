package com.mealtracker.meal_tracker_api.meals.repository;

import com.mealtracker.meal_tracker_api.meals.entity.Meal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, UUID> {

    List<Meal> findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(
            UUID userId, Instant from, Instant to);

    List<Meal> findTop10ByUserIdOrderByLoggedAtDesc(UUID userId);
}
