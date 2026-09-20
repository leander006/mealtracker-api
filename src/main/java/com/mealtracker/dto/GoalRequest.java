package com.mealtracker.dto;

import jakarta.validation.constraints.*;

/** What the user submits on the Goals screen. proteinPct/carbsPct/fatPct
 *  are optional - if omitted we fall back to a sensible default split
 *  (30/40/30) rather than requiring the user to think about macros. */
public record GoalRequest(
    @NotBlank @Pattern(regexp = "male|female") String sex,
    @NotNull @Min(13) @Max(100) Integer age,
    @NotNull @Positive Double heightCm,
    @NotNull @Positive Double weightKg,
    @NotBlank @Pattern(regexp = "sedentary|light|moderate|active|very_active") String activityLevel,
    @NotBlank @Pattern(regexp = "lose|maintain|gain") String goalType,
    @NotNull @DecimalMin("0.0") @DecimalMax("1.5") Double weeklyRateKg,
    Integer proteinPct,
    Integer carbsPct,
    Integer fatPct
) {}
