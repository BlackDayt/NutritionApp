package io.nutritionapp.datapipeline.dto.spoonacular;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class SpoonacularNutritionDto {
    private List<SpoonacularNutrientDto> nutrients;

    public Integer findValueByName(String name) {
        return nutrients.stream()
                .filter(n -> n.getName().equalsIgnoreCase(name))
                .map(n -> (int) Math.round(n.getAmount()))
                .findFirst()
                .orElse(null);
    }
}
