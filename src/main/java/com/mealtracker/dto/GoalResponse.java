package com.mealtracker.dto;

public record GoalResponse(
    String sex,
    Integer age,
    Double heightCm,
    Double weightKg,
    String activityLevel,
    String goalType,
    Double weeklyRateKg,
    Integer proteinPct,
    Integer carbsPct,
    Integer fatPct,
    Integer calorieGoal,
    Double proteinGoalG,
    Double carbsGoalG,
    Double fatGoalG,
    Double bmr,
    Double tdee
) {}
