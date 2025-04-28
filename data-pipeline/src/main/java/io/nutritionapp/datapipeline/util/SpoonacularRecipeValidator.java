package io.nutritionapp.datapipeline.util;

import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularNutritionDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularRecipeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class SpoonacularRecipeValidator {

    public List<String> validate(SpoonacularRecipeDto dto) {
        List<String> errors = new ArrayList<>();

        if (isBlank(dto.getTitle())) {
            errors.add("Название рецепта отсутствует");
        }

        if (isBlank(dto.getInstructions())) {
            errors.add("Инструкция отсутствует");
        }

        if (dto.getExtendedIngredients() == null || dto.getExtendedIngredients().isEmpty()) {
            errors.add("Ингредиенты отсутствуют");
        } else if (dto.getExtendedIngredients().stream().anyMatch(i -> isBlank(i.getOriginal()))) {
            errors.add("Список ингредиентов содержит пустые элементы");
        }

        if (dto.getServings() == null || dto.getServings() <= 0) {
            errors.add("Количество порций должно быть положительным");
        }

        if (dto.getReadyInMinutes() == null || dto.getReadyInMinutes() <= 0) {
            errors.add("Время приготовления должно быть положительным");
        }

        validateNutrition(dto.getNutrition(), errors);

        return errors;
    }

    private void validateNutrition(SpoonacularNutritionDto nutrition, List<String> errors) {
        if (nutrition == null || nutrition.getNutrients() == null) {
            errors.add("Отсутствует блок с нутриентами");
            return;
        }

        if (nutrition.findValueByName("Calories") == null) {
            errors.add("Калории не указаны");
        }
        if (nutrition.findValueByName("Protein") == null) {
            errors.add("Белки не указаны");
        }
        if (nutrition.findValueByName("Fat") == null) {
            errors.add("Жиры не указаны");
        }
        if (nutrition.findValueByName("Carbohydrates") == null) {
            errors.add("Углеводы не указаны");
        }
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }
}

