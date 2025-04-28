package io.nutritionapp.datapipeline.dto.spoonacular;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpoonacularRecipeDto {
    private String title;
    private String summary;
    private String image;
    private String instructions;
    private Integer readyInMinutes;
    private Integer servings;

    private List<String> cuisines;
    private List<String> dishTypes;
    private List<SpoonacularIngredientDto> extendedIngredients;
    private SpoonacularNutritionDto nutrition;
}
