package io.nutritionapp.datapipeline.validators;


import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularIngredientDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularNutrientDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularNutritionDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularRecipeDto;
import io.nutritionapp.datapipeline.util.SpoonacularRecipeValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpoonacularRecipeValidatorTest {

    private final SpoonacularRecipeValidator validator = new SpoonacularRecipeValidator();

    @Test
    void shouldReturnEmptyErrorsWhenRecipeIsValid() {
        SpoonacularRecipeDto dto = new SpoonacularRecipeDto();
        dto.setTitle("Test Recipe");
        dto.setInstructions("Cook well");
        dto.setServings(2);
        dto.setReadyInMinutes(15);
        dto.setExtendedIngredients(List.of(new SpoonacularIngredientDto("Tomato")));

        SpoonacularNutritionDto nutrition = new SpoonacularNutritionDto();
        nutrition.setNutrients(List.of(
                new SpoonacularNutrientDto("Calories", 300.0, "kcal"),
                new SpoonacularNutrientDto("Protein", 10.0, "g"),
                new SpoonacularNutrientDto("Fat", 5.0, "g"),
                new SpoonacularNutrientDto("Carbohydrates", 20.0, "g")
        ));
        dto.setNutrition(nutrition);

        var errors = validator.validate(dto);
        assertTrue(errors.isEmpty());
    }

    @Test
    void shouldDetectMissingTitleAndInstructions() {
        SpoonacularRecipeDto dto = new SpoonacularRecipeDto();
        dto.setExtendedIngredients(List.of(new SpoonacularIngredientDto("Salt")));
        dto.setServings(1);
        dto.setReadyInMinutes(10);
        dto.setNutrition(new SpoonacularNutritionDto(List.of(
                new SpoonacularNutrientDto("Calories", 100.0, "kcal"),
                new SpoonacularNutrientDto("Protein", 3.0, "g"),
                new SpoonacularNutrientDto("Fat", 2.0, "g"),
                new SpoonacularNutrientDto("Carbohydrates", 15.0, "g")
        )));

        var errors = validator.validate(dto);
        assertTrue(errors.contains("Название рецепта отсутствует"));
        assertTrue(errors.contains("Инструкция отсутствует"));
    }

    @Test
    void shouldFailIfIngredientsMissing() {
        SpoonacularRecipeDto dto = new SpoonacularRecipeDto();
        dto.setTitle("Test");
        dto.setInstructions("Boil");
        dto.setServings(2);
        dto.setReadyInMinutes(15);
        dto.setNutrition(new SpoonacularNutritionDto(List.of(
                new SpoonacularNutrientDto("Calories", 100.0, "kcal"),
                new SpoonacularNutrientDto("Protein", 5.0, "g"),
                new SpoonacularNutrientDto("Fat", 1.0, "g"),
                new SpoonacularNutrientDto("Carbohydrates", 20.0, "g")
        )));

        var errors = validator.validate(dto);
        assertTrue(errors.contains("Ингредиенты отсутствуют"));
    }

    @Test
    void shouldDetectMissingNutritionValues() {
        SpoonacularRecipeDto dto = new SpoonacularRecipeDto();
        dto.setTitle("Salad");
        dto.setInstructions("Mix well");
        dto.setServings(2);
        dto.setReadyInMinutes(10);
        dto.setExtendedIngredients(List.of(new SpoonacularIngredientDto("Lettuce")));
        dto.setNutrition(new SpoonacularNutritionDto(List.of(
                new SpoonacularNutrientDto("Calories", 80.0, "kcal"),
                new SpoonacularNutrientDto("Fat", 2.0, "g")
                // Protein and Carbohydrates missing
        )));

        var errors = validator.validate(dto);
        assertTrue(errors.contains("Белки не указаны"));
        assertTrue(errors.contains("Углеводы не указаны"));
    }
}