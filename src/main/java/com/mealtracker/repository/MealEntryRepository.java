package com.mealtracker.repository;

import com.mealtracker.entity.MealEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.Instant;
import java.util.List;

public interface MealEntryRepository extends JpaRepository<MealEntry, Long> {
    List<MealEntry> findByUserIdAndLoggedAtBetweenOrderByLoggedAtDesc(Long userId, Instant from, Instant to);
    List<MealEntry> findByUserIdOrderByLoggedAtDesc(Long userId);
}
