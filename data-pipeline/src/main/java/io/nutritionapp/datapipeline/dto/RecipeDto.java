package io.nutritionapp.datapipeline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    private Long externalId;
    private Long publicId;
    private String name;
    private String description;

    private List<String> tags; // переведенные dishTypes + cuisines
    private List<String> ingredients; // переведенные original-строки

    private Integer calories;
    private Integer proteins;
    private Integer fats;
    private Integer carbohydrates;

    private Integer cookTime;
    private Integer servings;

    private String instructions;
    private String imageUrl;
}
