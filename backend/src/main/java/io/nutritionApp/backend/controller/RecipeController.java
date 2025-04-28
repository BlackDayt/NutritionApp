package io.nutritionapp.backend.controller;

import io.nutritionapp.backend.dto.recipe.RecipeResponse;
import io.nutritionapp.backend.model.entity.Recipe;
import io.nutritionapp.backend.model.entity.User;
import io.nutritionapp.backend.service.RecipeService;
import io.nutritionapp.backend.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recipes")
@Slf4j
public class RecipeController {
    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }

    // Получить случайный рецепт для пользователя с учетом предпочтений
    @GetMapping("/random")
    public ResponseEntity<RecipeResponse> getRandomRecipeForUser(@RequestParam(name = "telegramId") Long telegramId) {
        log.info("Получение случайного рецепта для пользователя telegramId: {}", telegramId);
        User user = userService.findByTelegramId(telegramId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        return recipeService.findRandomRecipeForUser(user)
                .map(recipe -> ResponseEntity.ok(toRecipeResponse(recipe)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Поиск по тегам
    @GetMapping("/by-tags")
    public ResponseEntity<List<RecipeResponse>> getRecipesByTags(@RequestParam(name = "tagIds") List<UUID> tagIds) {
        log.info("Поиск рецептов по тегам: {}", tagIds);
        List<Recipe> recipes = recipeService.findByRecipeTags(tagIds);
        return ResponseEntity.ok(recipes.stream().map(this::toRecipeResponse).toList());
    }

    // Поиск с исключением ингредиентов
    @GetMapping("/by-excluded-ingredients")
    public ResponseEntity<List<RecipeResponse>> getRecipesByExcludedIngredients(
            @RequestParam(name = "excludedIngredientIds") List<UUID> excludedIngredientIds
    ) {
        log.info("Поиск рецептов с исключением ингредиентов: {}", excludedIngredientIds);
        List<Recipe> recipes = recipeService.findByExcludedIngredients(excludedIngredientIds);
        return ResponseEntity.ok(recipes.stream().map(this::toRecipeResponse).toList());
    }

    // Поиск по диапазону калорий
    @GetMapping("/by-calories")
    public ResponseEntity<List<RecipeResponse>> getRecipesByCalories(
            @RequestParam(name = "minCalories") @Min(0) Integer minCalories,
            @RequestParam(name = "maxCalories") @Max(5000) Integer maxCalories) {
        log.info("Поиск рецептов по калориям от {} до {}", minCalories, maxCalories);
        List<Recipe> recipes = recipeService.findByCaloriesRange(minCalories, maxCalories);
        return ResponseEntity.ok(recipes.stream().map(this::toRecipeResponse).toList());
    }

    // Маппер
    private RecipeResponse toRecipeResponse(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getCalories(),
                recipe.getProteins(),
                recipe.getFats(),
                recipe.getCarbohydrates(),
                recipe.getCookTime(),
                recipe.getServings(),
                recipe.getInstructions(),
                recipe.getImageUrl(),
                recipe.getRecipeTags().stream()
                        .map(rt -> rt.getTag().getName())
                        .collect(Collectors.toList()),
                recipe.getRecipeIngredients().stream()
                        .map(ri -> ri.getIngredient().getName())
                        .collect(Collectors.toList())
        );
    }
}
