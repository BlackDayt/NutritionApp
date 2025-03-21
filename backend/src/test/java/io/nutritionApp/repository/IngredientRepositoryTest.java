package io.nutritionApp.repository;

import io.nutritionApp.model.entity.Ingredient;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class IngredientRepositoryTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void shouldFindIngredientByName() {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByName("Тофу");

        assertThat(ingredientOpt).isPresent();
        assertThat(ingredientOpt.get().getName()).isEqualTo("Тофу");
    }

    @Test
    void shouldReturnEmptyIfIngredientNotFound() {
        Optional<Ingredient> ingredientOpt = ingredientRepository.findByName("Несуществующий ингредиент");
        assertThat(ingredientOpt).isEmpty();
    }
}
