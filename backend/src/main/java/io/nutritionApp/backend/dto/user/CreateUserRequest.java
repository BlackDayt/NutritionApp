package io.nutritionapp.backend.dto.user;

import io.nutritionapp.backend.model.enums.ActivityLevel;
import io.nutritionapp.backend.model.enums.DietGoal;
import io.nutritionapp.backend.model.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CreateUserRequest {

    @NotNull
    private Long telegramId;

    @NotBlank
    private String name;

    @NotNull
    private Gender gender;

    @Min(1)
    private Integer age;

    @Positive
    private double weight;

    @Positive
    private double height;

    @NotNull
    private ActivityLevel activityLevel;

    @NotNull
    private DietGoal dietGoal;

    @Min(1)
    private int mealCount;

    @NotNull
    private List<UUID> preferredTagIds;

    @NotNull
    private List<UUID> excludedIngredientIds;
}
