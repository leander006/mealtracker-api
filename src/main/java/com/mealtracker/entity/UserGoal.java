package com.mealtracker.entity;

import jakarta.persistence.*;
import java.time.Instant;

/** One row per user - their body stats + goal preferences, and the
 *  calorie/macro targets last calculated from them. Recalculated
 *  every time the profile is saved (see GoalCalculator). */
@Entity
@Table(name = "user_goals")
public class UserGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(nullable = false)
    private String sex; // "male" | "female"

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private Double heightCm;

    @Column(nullable = false)
    private Double weightKg;

    @Column(nullable = false)
    private String activityLevel; // sedentary | light | moderate | active | very_active

    @Column(nullable = false)
    private String goalType; // lose | maintain | gain

    @Column(nullable = false)
    private Double weeklyRateKg; // magnitude only, e.g. 0.5 - direction comes from goalType

    @Column(nullable = false)
    private Integer proteinPct = 30;

    @Column(nullable = false)
    private Integer carbsPct = 40;

    @Column(nullable = false)
    private Integer fatPct = 30;

    // --- computed + cached alongside the inputs that produced them ---
    @Column(nullable = false)
    private Integer calorieGoal;

    @Column(nullable = false)
    private Double proteinGoalG;

    @Column(nullable = false)
    private Double carbsGoalG;

    @Column(nullable = false)
    private Double fatGoalG;

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }
    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }
    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }
    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }
    public Double getWeeklyRateKg() { return weeklyRateKg; }
    public void setWeeklyRateKg(Double weeklyRateKg) { this.weeklyRateKg = weeklyRateKg; }
    public Integer getProteinPct() { return proteinPct; }
    public void setProteinPct(Integer proteinPct) { this.proteinPct = proteinPct; }
    public Integer getCarbsPct() { return carbsPct; }
    public void setCarbsPct(Integer carbsPct) { this.carbsPct = carbsPct; }
    public Integer getFatPct() { return fatPct; }
    public void setFatPct(Integer fatPct) { this.fatPct = fatPct; }
    public Integer getCalorieGoal() { return calorieGoal; }
    public void setCalorieGoal(Integer calorieGoal) { this.calorieGoal = calorieGoal; }
    public Double getProteinGoalG() { return proteinGoalG; }
    public void setProteinGoalG(Double proteinGoalG) { this.proteinGoalG = proteinGoalG; }
    public Double getCarbsGoalG() { return carbsGoalG; }
    public void setCarbsGoalG(Double carbsGoalG) { this.carbsGoalG = carbsGoalG; }
    public Double getFatGoalG() { return fatGoalG; }
    public void setFatGoalG(Double fatGoalG) { this.fatGoalG = fatGoalG; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
