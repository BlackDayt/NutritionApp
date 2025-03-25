package io.nutritionApp.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionApp.model.MealPlan;
import jakarta.persistence.AttributeConverter;
import org.apache.commons.lang3.StringUtils;
import jakarta.persistence.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

@Converter(autoApply = true)
public class MealPlanConverter implements AttributeConverter<MealPlan, String> {

    private static final Logger log = LoggerFactory.getLogger(MealPlanConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public String convertToDatabaseColumn(MealPlan mealPlan) {
        String someClassJson = null;
        try {
            someClassJson = objectMapper.writeValueAsString(mealPlan.getMeals()); //getMeals---
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }

        return someClassJson;
    }

    @Override
    public MealPlan convertToEntityAttribute(String json) {
        Map<String, Double> map = null;
        if (StringUtils.isBlank(json)) {
            return null;
        }
        try {
            map = objectMapper.readValue(json, new TypeReference<>() {});
//            mealPlan = objectMapper.readValue(json, MealPlan.class);

        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return new MealPlan(map);
    }
}
