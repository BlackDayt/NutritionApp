package io.nutritionapp.backend.dto.ingredient;

import java.util.UUID;

public record IngredientResponse(
        UUID id,
        String name
) {
}
