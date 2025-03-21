package io.nutritionApp.repository;

import io.nutritionApp.model.entity.Recipe;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void shouldFindRecipesByTags() {
        List<UUID> recipeTags = List.of(
                tagRepository.findByName("Веганская").orElseThrow().getId(),
                tagRepository.findByName("Безглютеновая").orElseThrow().getId()
        );
        List<Recipe> result = recipeRepository.findByRecipeTags(recipeTags);
        assertThat(result).isNotEmpty();


        for (Recipe recipe : result) {
            boolean hasMatchingTag = recipe.getRecipeTags().stream()
                    .map(recipeTag -> recipeTag.getTag().getId())
                    .anyMatch(recipeTags::contains);

            assertThat(hasMatchingTag)
                    .withFailMessage("Рецепт %s не содержит ни одного из тегов %s", recipe.getName(), recipeTags)
                    .isTrue();
        }
    }

    @Test
    void shouldFindRecipesByExcludedIngredients() {
        List<UUID> excludedIngredients = List.of(ingredientRepository.findByName("Глютен").orElseThrow().getId());
        List<Recipe> result = recipeRepository.findByExcludedIngredients(excludedIngredients);
        assertThat(result).isNotEmpty();

        for (Recipe recipe : result) {
            boolean hasExcluded = recipe.getRecipeIngredients().stream()
                    .map(ri -> ri.getIngredient().getId())
                    .anyMatch(excludedIngredients::contains);

            assertThat(hasExcluded)
                    .withFailMessage("Рецепт %s содержит исключенный ингредиент!", recipe.getName())
                    .isFalse();
        }

    }

    @Test
    void shouldFindRecipesByCaloriesRange() {
        List<Recipe> result = recipeRepository.findByCaloriesBetween(100, 300);
        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getName()).isEqualTo("Веганский суп");
    }

    @Test
    void shouldFindRandomRecipe() {
        Recipe result = recipeRepository.findRandomRecipe();
        assertThat(result).isNotNull();
    }
}
