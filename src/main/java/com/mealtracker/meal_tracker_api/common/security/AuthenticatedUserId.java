package com.mealtracker.meal_tracker_api.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a controller method parameter to be resolved as the authenticated
 * user's ID (set earlier by JwtAuthFilter). Usage:
 *
 *   @PostMapping
 *   public ResponseEntity<X> foo(@AuthenticatedUserId UUID userId, ...)
 *
 * This is nicer than reading the request attribute manually in every
 * controller method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface AuthenticatedUserId {
}
