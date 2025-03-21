package io.nutritionApp.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionApp.model.MealPlan;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

@Converter
public class MealPlanConverter implements AttributeConverter<MealPlan, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(MealPlan mealPlan) {
        if (mealPlan == null || mealPlan.getMeals() == null) {
            return "{}";
        }
        try {
            return objectMapper.writeValueAsString(mealPlan.getMeals());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Ошибка сериализации в JSON", e);
        }
    }

    @Override
    public MealPlan convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) {
            return new MealPlan();
        }
        try {
            Map<String, Double> meals = objectMapper.readValue(json, new TypeReference<>() {});
            return new MealPlan(meals);
        } catch (IOException e) {
            throw new IllegalArgumentException("Ошибка десериализации JSON", e);
        }
    }
}
