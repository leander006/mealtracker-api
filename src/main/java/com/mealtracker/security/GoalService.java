package com.mealtracker.service;

import com.mealtracker.dto.GoalRequest;
import com.mealtracker.dto.GoalResponse;
import com.mealtracker.entity.UserGoal;
import com.mealtracker.repository.UserGoalRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class GoalService {

    private final UserGoalRepository userGoalRepository;

    public GoalService(UserGoalRepository userGoalRepository) {
        this.userGoalRepository = userGoalRepository;
    }

    public Optional<GoalResponse> getGoals(Long userId) {
        return userGoalRepository.findByUserId(userId).map(this::toResponse);
    }

    public GoalResponse saveGoals(Long userId, GoalRequest request) {
        UserGoal goal = userGoalRepository.findByUserId(userId).orElseGet(UserGoal::new);

        int proteinPct = request.proteinPct() != null ? request.proteinPct() : 30;
        int carbsPct = request.carbsPct() != null ? request.carbsPct() : 40;
        int fatPct = request.fatPct() != null ? request.fatPct() : 30;
        if (proteinPct + carbsPct + fatPct != 100) {
            // Don't fail the request over rounding - normalize back to defaults.
            proteinPct = 30; carbsPct = 40; fatPct = 30;
        }

        GoalCalculator.Result result = GoalCalculator.calculate(
            request.sex(), request.age(), request.heightCm(), request.weightKg(),
            request.activityLevel(), request.goalType(), request.weeklyRateKg(),
            proteinPct, carbsPct, fatPct
        );

        goal.setUserId(userId);
        goal.setSex(request.sex());
        goal.setAge(request.age());
        goal.setHeightCm(request.heightCm());
        goal.setWeightKg(request.weightKg());
        goal.setActivityLevel(request.activityLevel());
        goal.setGoalType(request.goalType());
        goal.setWeeklyRateKg(request.weeklyRateKg());
        goal.setProteinPct(proteinPct);
        goal.setCarbsPct(carbsPct);
        goal.setFatPct(fatPct);
        goal.setCalorieGoal(result.calorieGoal());
        goal.setProteinGoalG(result.proteinGoalG());
        goal.setCarbsGoalG(result.carbsGoalG());
        goal.setFatGoalG(result.fatGoalG());
        goal.setUpdatedAt(Instant.now());

        UserGoal saved = userGoalRepository.save(goal);
        return toResponse(saved, result.bmr(), result.tdee());
    }

    private GoalResponse toResponse(UserGoal g) {
        // bmr/tdee aren't persisted (only the inputs + resulting targets are) -
        // recompute them for display since they're cheap and always derivable.
        GoalCalculator.Result r = GoalCalculator.calculate(
            g.getSex(), g.getAge(), g.getHeightCm(), g.getWeightKg(),
            g.getActivityLevel(), g.getGoalType(), g.getWeeklyRateKg(),
            g.getProteinPct(), g.getCarbsPct(), g.getFatPct()
        );
        return toResponse(g, r.bmr(), r.tdee());
    }

    private GoalResponse toResponse(UserGoal g, double bmr, double tdee) {
        return new GoalResponse(
            g.getSex(), g.getAge(), g.getHeightCm(), g.getWeightKg(),
            g.getActivityLevel(), g.getGoalType(), g.getWeeklyRateKg(),
            g.getProteinPct(), g.getCarbsPct(), g.getFatPct(),
            g.getCalorieGoal(), g.getProteinGoalG(), g.getCarbsGoalG(), g.getFatGoalG(),
            bmr, tdee
        );
    }
}
