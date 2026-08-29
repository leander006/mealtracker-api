package com.mealtracker.meal_tracker_api.meals.service;

import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.LogMealRequest;
import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.MacrosDto;
import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.MealResponse;
import com.mealtracker.meal_tracker_api.meals.dto.MealDtos.WeeklySummaryResponse;
import com.mealtracker.meal_tracker_api.meals.entity.Meal;
import com.mealtracker.meal_tracker_api.meals.repository.MealRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MealService {

    private static final Logger log = LoggerFactory.getLogger(MealService.class);
    private static final int SUMMARY_WINDOW_DAYS = 7;

    private final MealRepository mealRepository;

    public MealResponse logMeal(UUID userId, LogMealRequest request) {
        Meal meal = Meal.builder()
                .userId(userId)
                .foodLabel(request.foodLabel())
                .portionGrams(request.portionGrams())
                .calories(request.macros().calories())
                .proteinG(request.macros().proteinG())
                .carbsG(request.macros().carbsG())
                .fatG(request.macros().fatG())
                .fiberG(request.macros().fiberG())
                .imageUrl(request.imageUrl())
                .loggedAt(request.loggedAt())
                .build();

        Meal saved = mealRepository.save(meal);
        log.info("Meal logged, mealId={}, userId={}", saved.getId(), userId);
        return toResponse(saved);
    }

    public List<MealResponse> getMeals(UUID userId, Instant from, Instant to) {
        return mealRepository
                .findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(userId, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Powers the "quick log" / "log again" flow discussed earlier - lets
    // the frontend show recent meals as one-tap re-log options without
    // running the AI pipeline again.
    public List<MealResponse> getRecentMeals(UUID userId) {
        return mealRepository.findTop10ByUserIdOrderByLoggedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WeeklySummaryResponse getWeeklySummary(UUID userId) {
        Instant to = Instant.now();
        Instant from = to.minusSeconds(SUMMARY_WINDOW_DAYS * 24 * 3600L);

        List<Meal> meals = mealRepository
                .findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(userId, from, to);

        if (meals.isEmpty()) {
            return new WeeklySummaryResponse(0, 0, 0, 0, 0, 0);
        }

        double totalCalories = meals.stream().mapToDouble(Meal::getCalories).sum();
        double totalProtein = meals.stream().mapToDouble(Meal::getProteinG).sum();
        double totalCarbs = meals.stream().mapToDouble(Meal::getCarbsG).sum();
        double totalFat = meals.stream().mapToDouble(Meal::getFatG).sum();
        double totalFiber = meals.stream().mapToDouble(Meal::getFiberG).sum();

        return new WeeklySummaryResponse(
                totalCalories / SUMMARY_WINDOW_DAYS,
                totalProtein / SUMMARY_WINDOW_DAYS,
                totalCarbs / SUMMARY_WINDOW_DAYS,
                totalFat / SUMMARY_WINDOW_DAYS,
                totalFiber / SUMMARY_WINDOW_DAYS,
                meals.size()
        );
    }

    public void deleteMeal(UUID userId, UUID mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(MealNotFoundException::new);

        // Ownership check - a user must never be able to delete another
        // user's meal just by guessing/enumerating meal IDs.
        if (!meal.getUserId().equals(userId)) {
            log.warn("User {} attempted to delete meal {} owned by another user", userId, mealId);
            throw new MealNotFoundException();
        }

        mealRepository.delete(meal);
        log.info("Meal deleted, mealId={}, userId={}", mealId, userId);
    }

    private MealResponse toResponse(Meal meal) {
        return new MealResponse(
                meal.getId(),
                meal.getFoodLabel(),
                meal.getPortionGrams(),
                new MacrosDto(meal.getCalories(), meal.getProteinG(),
                        meal.getCarbsG(), meal.getFatG(), meal.getFiberG()),
                meal.getImageUrl(),
                meal.getLoggedAt()
        );
    }
}
