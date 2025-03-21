package io.nutritionApp.service;

import io.nutritionApp.model.entity.Recipe;
import io.nutritionApp.model.entity.User;
import io.nutritionApp.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class RecipeService {
    private static final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;

    private final Random random = new Random();

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Получает случайный рецепт для пользователя, учитывая его предпочтения и исключенные ингредиенты.
     */
    public Optional<Recipe> findRandomRecipeForUser(User user) {
        log.debug("Получение рандомного рецепта для пользователя: {}", user.getTelegramId());

        List<UUID> preferredTags = user.getPreferredTags().stream()
                .map(userPreferredTag -> userPreferredTag.getTag().getId()).toList();

        List<UUID> excludedIngredients = user.getExcludedIngredients().stream()
                .map(userExcludedIngredient -> userExcludedIngredient.getIngredient().getId()).toList();

        // Получаем список рецептов, соответствующих тегам
        List<Recipe> recipes = recipeRepository.findByRecipeTags(preferredTags);

        // Фильтруем рецепты, исключая те, которые содержат запрещенные ингредиенты
        List<Recipe> filteredRecipes = recipes.stream()
                .filter(recipe -> recipe.getRecipeIngredients().stream()
                        .noneMatch(recipeIngredient -> excludedIngredients.contains(recipeIngredient.getIngredient().getId()))
                )
                .toList();

        // Если после фильтрации остались рецепты, выбираем случайный
        if (filteredRecipes.isEmpty()) {
            log.warn("Нет подходящих рецептов для пользователя {}", user.getTelegramId());
            return Optional.empty();
        }
        Recipe randomRecipe = filteredRecipes.get(random.nextInt(filteredRecipes.size()));
        log.info("Рецепт для пользователя {}: {}", user.getTelegramId(), randomRecipe.getName());
        return Optional.of(randomRecipe);
    }
}
