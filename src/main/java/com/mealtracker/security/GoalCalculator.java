package com.mealtracker.service;

import java.util.Map;

/** Calorie/macro target math, following the same approach most consumer
 *  fitness apps (MyFitnessPal, Cronometer, etc.) use:
 *
 *   1. BMR via the Mifflin-St Jeor equation (the modern standard - more
 *      accurate across body types than the older Harris-Benedict formula).
 *   2. TDEE = BMR * an activity multiplier.
 *   3. Target calories = TDEE, shifted by the daily deficit/surplus implied
 *      by the user's desired weekly weight change (~7700 kcal per kg of
 *      body fat), then floored at a safe minimum.
 *   4. Macros split from target calories by user-chosen (or default
 *      30/40/30 protein/carb/fat) percentages.
 */
public final class GoalCalculator {

    private static final double KCAL_PER_KG_FAT = 7700.0;
    private static final int MIN_CALORIES_MALE = 1500;
    private static final int MIN_CALORIES_FEMALE = 1200;

    private static final Map<String, Double> ACTIVITY_MULTIPLIERS = Map.of(
        "sedentary", 1.2,
        "light", 1.375,
        "moderate", 1.55,
        "active", 1.725,
        "very_active", 1.9
    );

    private GoalCalculator() {}

    public record Result(
        double bmr, double tdee, int calorieGoal,
        double proteinGoalG, double carbsGoalG, double fatGoalG
    ) {}

    public static Result calculate(
        String sex, int age, double heightCm, double weightKg,
        String activityLevel, String goalType, double weeklyRateKg,
        int proteinPct, int carbsPct, int fatPct
    ) {
        double bmr = "male".equals(sex)
            ? 10 * weightKg + 6.25 * heightCm - 5 * age + 5
            : 10 * weightKg + 6.25 * heightCm - 5 * age - 161;

        double multiplier = ACTIVITY_MULTIPLIERS.getOrDefault(activityLevel, 1.2);
        double tdee = bmr * multiplier;

        double dailyAdjustment = weeklyRateKg * KCAL_PER_KG_FAT / 7.0;
        double rawTarget = switch (goalType) {
            case "lose" -> tdee - dailyAdjustment;
            case "gain" -> tdee + dailyAdjustment;
            default -> tdee; // maintain
        };

        int minCalories = "male".equals(sex) ? MIN_CALORIES_MALE : MIN_CALORIES_FEMALE;
        int calorieGoal = (int) Math.round(Math.max(rawTarget, minCalories));

        double proteinGoalG = Math.round(calorieGoal * (proteinPct / 100.0) / 4.0 * 10) / 10.0;
        double carbsGoalG = Math.round(calorieGoal * (carbsPct / 100.0) / 4.0 * 10) / 10.0;
        double fatGoalG = Math.round(calorieGoal * (fatPct / 100.0) / 9.0 * 10) / 10.0;

        return new Result(Math.round(bmr * 10) / 10.0, Math.round(tdee * 10) / 10.0, calorieGoal, proteinGoalG, carbsGoalG, fatGoalG);
    }
}
