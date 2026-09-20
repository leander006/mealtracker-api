package com.mealtracker.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "meal_entries")
public class MealEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String foodDescription;

    // "scan" | "search" | "manual" - how this entry was created, useful
    // later for auditing model quality vs. manual entries
    @Column(nullable = false)
    private String source;

    private Long mlFoodId; // references foods.id in the ml-service's Postgres, not a JPA FK

    @Column(nullable = false)
    private Double quantityGrams;

    @Column(nullable = false)
    private Double caloriesKcal;
    private Double proteinG;
    private Double carbsG;
    private Double fatG;
    private Double fiberG;
    private Double sugarG;
    private Double sodiumMg;

    private Double detectionConfidence; // null for search/manual entries
    private String imageUrl;            // Cloudinary URL, for scan entries

    @Column(nullable = false, updatable = false)
    private Instant loggedAt = Instant.now();

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getFoodDescription() { return foodDescription; }
    public void setFoodDescription(String foodDescription) { this.foodDescription = foodDescription; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Long getMlFoodId() { return mlFoodId; }
    public void setMlFoodId(Long mlFoodId) { this.mlFoodId = mlFoodId; }
    public Double getQuantityGrams() { return quantityGrams; }
    public void setQuantityGrams(Double quantityGrams) { this.quantityGrams = quantityGrams; }
    public Double getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(Double caloriesKcal) { this.caloriesKcal = caloriesKcal; }
    public Double getProteinG() { return proteinG; }
    public void setProteinG(Double proteinG) { this.proteinG = proteinG; }
    public Double getCarbsG() { return carbsG; }
    public void setCarbsG(Double carbsG) { this.carbsG = carbsG; }
    public Double getFatG() { return fatG; }
    public void setFatG(Double fatG) { this.fatG = fatG; }
    public Double getFiberG() { return fiberG; }
    public void setFiberG(Double fiberG) { this.fiberG = fiberG; }
    public Double getSugarG() { return sugarG; }
    public void setSugarG(Double sugarG) { this.sugarG = sugarG; }
    public Double getSodiumMg() { return sodiumMg; }
    public void setSodiumMg(Double sodiumMg) { this.sodiumMg = sodiumMg; }
    public Double getDetectionConfidence() { return detectionConfidence; }
    public void setDetectionConfidence(Double detectionConfidence) { this.detectionConfidence = detectionConfidence; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Instant getLoggedAt() { return loggedAt; }
}
