package io.nutritionapp.backend.dto.recipe;

import java.util.List;
import java.util.UUID;

public record RecipeResponse(
        UUID id,
        String name,
        String description,
        Integer calories,
        Integer proteins,
        Integer fats,
        Integer carbohydrates,
        Integer cookTime,
        Integer servings,
        String instructions,
        String imageUrl,
        List<String> tags,
        List<String> ingredients
) {}
