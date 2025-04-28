package io.nutritionapp.datapipeline.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.datapipeline.dto.RecipeDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularRecipeDto;
import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ApiRecipeToRecipeDtoMapper {

    private final ObjectMapper objectMapper;

    public RecipeDto map(ApiRecipe apiRecipe, Long publicId) {
        try {
            SpoonacularRecipeDto dto = objectMapper.readValue(apiRecipe.getOriginalData(), SpoonacularRecipeDto.class);

            RecipeDto row = new RecipeDto();
            row.setPublicId(publicId);
            row.setExternalId(apiRecipe.getExternalId().longValue());
            row.setName(dto.getTitle());
            row.setDescription(dto.getSummary()); // можно парсить HTML или оставить как есть
            row.setCalories(dto.getNutrition().findValueByName("Calories"));
            row.setProteins(dto.getNutrition().findValueByName("Protein"));
            row.setFats(dto.getNutrition().findValueByName("Fat"));
            row.setCarbohydrates(dto.getNutrition().findValueByName("Carbohydrates"));
            row.setCookTime(dto.getReadyInMinutes());
            row.setServings(dto.getServings());
            row.setImageUrl(dto.getImage());
            row.setInstructions(dto.getInstructions());

            // Теги можно вытащить из cuisines/dishTypes
            row.setTags(Stream.concat(
                    dto.getCuisines().stream(),
                    dto.getDishTypes().stream()
            ).distinct().collect(Collectors.toList()));

            // Ингредиенты
            List<String> ingredients = dto.getExtendedIngredients().stream()
                    .map(i -> i.getOriginal())
                    .collect(Collectors.toList());
            row.setIngredients(ingredients);

            return row;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при маппинге рецепта с externalId: " + apiRecipe.getExternalId(), e);
        }
    }
}

