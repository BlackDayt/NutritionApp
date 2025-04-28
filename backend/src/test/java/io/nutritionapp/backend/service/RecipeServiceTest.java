package io.nutritionapp.backend.service;

import io.nutritionapp.backend.model.entity.Recipe;
import io.nutritionapp.backend.model.entity.User;
import io.nutritionapp.backend.repository.RecipeRepository;
import io.nutritionapp.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


//@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void shouldReturnRandomRecipeForUserWithPreferences() {
        User user = userRepository.findByTelegramId(1234567890L).orElseThrow();
        System.out.println(user.getName());

        Optional<Recipe> result = recipeService.findRandomRecipeForUser(user);
        System.out.println(result);

        assertThat(result).isPresent();
        assertThat(result.get().getRecipeTags().stream().map(recipeTag -> recipeTag.getTag().getId())
                .anyMatch(tag -> user.getPreferredTags()
                        .stream()
                        .anyMatch(ut -> ut.getTag().getId().equals(tag))));
    }

    @Test
    void shouldReturnEmptyIfNoRecipesMatchUserPreferences() {
        User user = userRepository.findByTelegramId(1234567890L).orElseThrow();

        // Удаляем все теги у рецептов
        recipeRepository.findAll().forEach(recipe -> {
            recipe.getRecipeTags().clear();
            recipeRepository.save(recipe);
        });

        Optional<Recipe> result = recipeService.findRandomRecipeForUser(user);

        assertThat(result).isEmpty();
    }
}

