package io.nutritionapp.datapipeline.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


import io.nutritionapp.datapipeline.dto.RecipeDto;


@Component
@Slf4j
public class RecipeDtoValidator {
    private final Set<String> allowedTags;

    public RecipeDtoValidator(@Value("${tags.whitelist.path}") Resource tagFile,
                              ObjectMapper objectMapper) {
        try (InputStream is = tagFile.getInputStream()) {
            List<String> list = objectMapper.readValue(is, new TypeReference<>() {});
            this.allowedTags = new HashSet<>(list);
        } catch (IOException e) {
            log.error("Не удалось загрузить список разрешённых тегов", e);
            throw new IllegalStateException("Ошибка загрузки конфигурации тегов");
        }
    }


    public boolean isValid(RecipeDto recipe) {
        return validateAndReport(recipe).isEmpty();
    }

    public List<String> validateAndReport(RecipeDto recipe) {
        List<String> errors = new ArrayList<>();

        if (isBlank(recipe.getName())) {
            errors.add("Название рецепта отсутствует");
        }

        if (isBlank(recipe.getInstructions())) {
            errors.add("Инструкция отсутствует");
        }

        if (recipe.getIngredients() == null || recipe.getIngredients().isEmpty()) {
            errors.add("Список ингредиентов пуст");
        } else if (recipe.getIngredients().stream().anyMatch(this::isBlank)) {
            errors.add("Список ингредиентов содержит пустые элементы");
        }

        // Проверка тегов
        if (recipe.getTags() == null || recipe.getTags().isEmpty()) {
            errors.add("Список тегов пуст");
        } else {
            List<String> filteredTags = recipe.getTags().stream()
                    .filter(tag -> !isBlank(tag) && allowedTags.contains(tag.toLowerCase()))
                    .collect(Collectors.toList());

            if (filteredTags.isEmpty()) {
                errors.add("Нет допустимых тегов среди предоставленных");
            }

            recipe.setTags(filteredTags); // обновляем в объекте
        }

        if (recipe.getServings() == null || recipe.getServings() <= 0) {
            errors.add("Количество порций должно быть положительным");
        }

        if (recipe.getCookTime() == null || recipe.getCookTime() <= 0) {
            errors.add("Время приготовления должно быть положительным");
        }

        if (isNegative(recipe.getCalories())) errors.add("Калории не могут быть отрицательными");
        if (isNegative(recipe.getProteins())) errors.add("Белки не могут быть отрицательными");
        if (isNegative(recipe.getFats())) errors.add("Жиры не могут быть отрицательными");
        if (isNegative(recipe.getCarbohydrates())) errors.add("Углеводы не могут быть отрицательными");

        return errors;
    }

    private boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    private boolean isNegative(Integer value) {
        return value != null && value < 0;
    }


    private Set<String> loadAllowedTagsFromJson() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config/allowed-tags.json")) {
            if (is == null) {
                log.warn("❗️Файл allowed-tags.json не найден, допустимые теги не загружены");
                return Collections.emptySet();
            }

            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> list = mapper.readValue(is, new TypeReference<>() {});
            return list.stream()
                    .map(entry -> entry.get("value").toLowerCase())
                    .collect(Collectors.toSet());

        } catch (Exception e) {
            log.error("Ошибка загрузки allowed-tags.json: {}", e.getMessage(), e);
            return Collections.emptySet();
        }
    }
}
