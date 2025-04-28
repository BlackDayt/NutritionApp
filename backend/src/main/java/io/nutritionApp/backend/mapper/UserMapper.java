package io.nutritionapp.backend.mapper;

import io.nutritionapp.backend.dto.user.CreateUserRequest;
import io.nutritionapp.backend.dto.user.UpdateUserRequest;
import io.nutritionapp.backend.dto.user.UserResponse;
import io.nutritionapp.backend.model.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toUser(CreateUserRequest request) {
        User user = User.builder()
            .telegramId(request.getTelegramId())
            .name(request.getName())
            .gender(request.getGender())
            .age(request.getAge())
            .weight(request.getWeight())
            .height(request.getHeight())
            .activityLevel(request.getActivityLevel())
            .dietGoal(request.getDietGoal())
            .mealCount(request.getMealCount())
            .build();

        user.updateGoalCalories();
        return user;
    }

    public void updateUserFromDto(User user, UpdateUserRequest request) {
        user.setName(request.getName());
        user.setGender(request.getGender());
        user.setAge(request.getAge());
        user.setWeight(request.getWeight());
        user.setHeight(request.getHeight());
        user.setActivityLevel(request.getActivityLevel());
        user.setDietGoal(request.getDietGoal());
        user.setMealCount(request.getMealCount());

        user.updateGoalCalories();
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getTelegramId(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getWeight(),
                user.getHeight(),
                user.getActivityLevel(),
                user.getDietGoal(),
                user.getGoalCalories(),
                user.getMealCount(),
                user.getMealPlan().getMeals(),
                user.getPreferredTags().stream().map(pt -> pt.getTag().getId()).toList(),
                user.getExcludedIngredients().stream().map(ei -> ei.getIngredient().getId()).toList()
        );
    }
}
