package com.mealtracker.service;

import com.mealtracker.dto.LogMealRequest;
import com.mealtracker.entity.MealEntry;
import com.mealtracker.repository.MealEntryRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class MealService {

    private final MealEntryRepository mealEntryRepository;

    public MealService(MealEntryRepository mealEntryRepository) {
        this.mealEntryRepository = mealEntryRepository;
    }

    public MealEntry logMeal(Long userId, LogMealRequest request) {
        MealEntry entry = new MealEntry();
        entry.setUserId(userId);
        entry.setFoodDescription(request.foodDescription());
        entry.setSource(request.source());
        entry.setMlFoodId(request.mlFoodId());
        entry.setQuantityGrams(request.quantityGrams());
        entry.setCaloriesKcal(request.caloriesKcal());
        entry.setProteinG(request.proteinG());
        entry.setCarbsG(request.carbsG());
        entry.setFatG(request.fatG());
        entry.setFiberG(request.fiberG());
        entry.setSugarG(request.sugarG());
        entry.setSodiumMg(request.sodiumMg());
        entry.setDetectionConfidence(request.detectionConfidence());
        entry.setImageUrl(request.imageUrl());
        return mealEntryRepository.save(entry);
    }

    public List<MealEntry> getToday(Long userId) {
        Instant startOfDay = Instant.now().truncatedTo(ChronoUnit.DAYS);
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);
        return mealEntryRepository.findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(userId, startOfDay, endOfDay);
    }

    public List<MealEntry> getHistory(Long userId) {
        return mealEntryRepository.findByUserIdOrderByLoggedAtDesc(userId);
    }
}
