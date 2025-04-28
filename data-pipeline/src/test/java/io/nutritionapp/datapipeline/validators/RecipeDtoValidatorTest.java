package io.nutritionapp.datapipeline.validators;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.datapipeline.dto.RecipeDto;
import io.nutritionapp.datapipeline.util.RecipeDtoValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecipeDtoValidatorTest {

    private RecipeDtoValidator validator;

    @BeforeEach
    void setUp() {
        ObjectMapper mapper = new ObjectMapper();
        ClassPathResource tagsFile = new ClassPathResource("config/allowed-tags.json");
        validator = new RecipeDtoValidator(tagsFile, mapper);
    }

    @Test
    void shouldPassWithValidData() {
        RecipeDto dto = RecipeDto.builder()
                .name("Тестовый рецепт")
                .instructions("Готовить тщательно")
                .ingredients(List.of("Помидор", "Огурец"))
                .tags(List.of("веганский", "кето")) // ✅ переведённые и допустимые
                .calories(300)
                .proteins(20)
                .fats(10)
                .carbohydrates(15)
                .cookTime(30)
                .servings(2)
                .build();

        List<String> errors = validator.validateAndReport(dto);

        assertTrue(errors.isEmpty());
        assertEquals(List.of("веганский", "кето"), dto.getTags());
    }

    @Test
    void shouldFailIfTagsInvalid() {
        RecipeDto dto = RecipeDto.builder()
                .name("Test")
                .instructions("Cook it well")
                .ingredients(List.of("Tomato"))
                .tags(List.of("alien food", "super healthy"))
                .calories(200)
                .proteins(10)
                .fats(5)
                .carbohydrates(10)
                .cookTime(15)
                .servings(1)
                .build();

        List<String> errors = validator.validateAndReport(dto);
        assertTrue(errors.contains("Нет допустимых тегов среди предоставленных"));
        assertTrue(dto.getTags().isEmpty());
    }

    @Test
    void shouldCatchEmptyFields() {
        RecipeDto dto = RecipeDto.builder().build();
        List<String> errors = validator.validateAndReport(dto);

        assertTrue(errors.contains("Название рецепта отсутствует"));
        assertTrue(errors.contains("Инструкция отсутствует"));
        assertTrue(errors.contains("Список ингредиентов пуст"));
        assertTrue(errors.contains("Список тегов пуст"));
        assertTrue(errors.contains("Количество порций должно быть положительным"));
        assertTrue(errors.contains("Время приготовления должно быть положительным"));
    }

    @Test
    void shouldCatchNegativeNutrition() {
        RecipeDto dto = RecipeDto.builder()
                .name("Test")
                .instructions("Test")
                .ingredients(List.of("Tomato"))
                .tags(List.of("vegan"))
                .calories(-1)
                .proteins(-1)
                .fats(-1)
                .carbohydrates(-1)
                .cookTime(5)
                .servings(1)
                .build();

        List<String> errors = validator.validateAndReport(dto);

        assertTrue(errors.contains("Калории не могут быть отрицательными"));
        assertTrue(errors.contains("Белки не могут быть отрицательными"));
        assertTrue(errors.contains("Жиры не могут быть отрицательными"));
        assertTrue(errors.contains("Углеводы не могут быть отрицательными"));
    }

    @Test
    void shouldFilterOutInvalidTagsButKeepValidOnes() {
        RecipeDto dto = RecipeDto.builder()
                .name("Test")
                .instructions("Test")
                .ingredients(List.of("Огурец"))
                .tags(List.of("веганский", "какой-то левый тег"))
                .cookTime(10)
                .servings(2)
                .build();

        List<String> errors = validator.validateAndReport(dto);

        assertFalse(errors.contains("Нет допустимых тегов среди предоставленных"));
        assertEquals(List.of("веганский"), dto.getTags());
    }

    @Test
    void shouldFailWhenTagsContainOnlyBlanksOrNulls() {
        RecipeDto dto = RecipeDto.builder()
                .name("Test")
                .instructions("Test")
                .ingredients(List.of("Огурец"))
                .tags(Arrays.asList(" ", "", null))
                .cookTime(10)
                .servings(2)
                .build();

        List<String> errors = validator.validateAndReport(dto);

        assertTrue(errors.contains("Нет допустимых тегов среди предоставленных"));
        assertTrue(dto.getTags().isEmpty());
    }

    @Test
    void shouldAcceptTagsRegardlessOfCase() {
        RecipeDto dto = RecipeDto.builder()
                .name("Test")
                .instructions("Test")
                .ingredients(List.of("Огурец"))
                .tags(List.of("ВЕГАНСКИЙ", "Кето"))
                .cookTime(10)
                .servings(2)
                .build();

        List<String> errors = validator.validateAndReport(dto);

        assertTrue(errors.isEmpty());
        assertEquals(List.of("ВЕГАНСКИЙ", "Кето"), dto.getTags()); // исходный регистр сохраняется
    }

}
