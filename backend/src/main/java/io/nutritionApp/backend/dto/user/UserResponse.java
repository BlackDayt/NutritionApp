package io.nutritionapp.backend.dto.user;

import io.nutritionapp.backend.model.enums.ActivityLevel;
import io.nutritionapp.backend.model.enums.DietGoal;
import io.nutritionapp.backend.model.enums.Gender;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UserResponse(
        UUID id,
        Long telegramId,
        String name,
        Gender gender,
        Integer age,
        double weight,
        double height,
        ActivityLevel activityLevel,
        DietGoal dietGoal,
        double goalCalories,
        int mealCount,
        Map<String, Double> mealPlan,
        List<UUID> preferredTagIds,
        List<UUID> excludedIngredientIds
) {}