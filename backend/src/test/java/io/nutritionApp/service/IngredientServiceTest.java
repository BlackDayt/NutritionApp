package io.nutritionApp.service;

import io.nutritionApp.model.entity.Ingredient;
import io.nutritionApp.repository.IngredientRepository;
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
class IngredientServiceTest {

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    void shouldFindAllIngredients() {
        var ingredients = ingredientService.findAll();
        assertThat(ingredients).isNotEmpty();
        assertThat(ingredients).extracting(Ingredient::getName).contains("Тофу", "Глютен", "Брокколи");
    }

    @Test
    void shouldFindIngredientById() {
        Ingredient ingredient = ingredientRepository.findByName("Тофу").orElseThrow();
        Optional<Ingredient> found = ingredientService.findById(ingredient.getId());
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Тофу");
    }

    @Test
    void shouldFindIngredientByName() {
        Optional<Ingredient> found = ingredientService.findByName("Брокколи");
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Брокколи");
    }

    @Test
    void shouldSaveIngredient() {
        Ingredient ingredient = new Ingredient("Морковь");
        Ingredient saved = ingredientService.save(ingredient);
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Морковь");
    }

    @Test
    void shouldDeleteIngredientById() {
        Ingredient ingredient = ingredientRepository.save(new Ingredient("Кабачок"));
        ingredientService.deleteById(ingredient.getId());
        Optional<Ingredient> found = ingredientRepository.findById(ingredient.getId());
        assertThat(found).isEmpty();
    }
}
