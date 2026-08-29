package com.mealtracker.meal_tracker_api.meals.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "meals")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {

    @Id
    @GeneratedValue
    private UUID id;

    // Not a @ManyToOne relation to User on purpose - meals and auth are
    // separate concerns even living in one app right now. Storing just the
    // userId keeps this entity independent, and matches how this would
    // split into two real microservices later without any rework.
    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String foodLabel;

    private Integer portionGrams;

    private Double calories;
    private Double proteinG;
    private Double carbsG;
    private Double fatG;
    private Double fiberG;

    private String imageUrl;

    @Column(nullable = false)
    private Instant loggedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (loggedAt == null) {
            loggedAt = Instant.now();
        }
    }
}
