package com.mealtracker.meal_tracker_api.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Nullable now - Google-authenticated users never set a password.
     * Enforce "must have either passwordHash OR googleId" at the service
     * layer, not with a DB constraint - Postgres CHECK constraints across
     * two nullable columns are possible but add complexity not worth it
     * at this scale.
     */
    @Column(nullable = true)
    private String passwordHash;

    /**
     * Google's unique, stable identifier for the user (the "sub" claim in
     * their ID token). Null for normal email/password accounts. This is
     * what we match on for repeat Google logins - NOT email alone, since
     * emails can theoretically be reused/changed on Google's side.
     */
    @Column(nullable = true, unique = true)
    private String googleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public enum AuthProvider {
        LOCAL, GOOGLE
    }
}
