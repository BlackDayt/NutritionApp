package io.nutritionapp.datapipeline.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.datapipeline.config.SpoonacularClientConfig;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularRecipeDto;
import io.nutritionapp.datapipeline.client.SpoonacularClient;
import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import io.nutritionapp.datapipeline.repository.ApiRecipeRepository;
import io.nutritionapp.datapipeline.util.SpoonacularRecipeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImportService {

    public final ApiRecipeRepository apiRecipeRepository;
    public final SpoonacularClient spoonacularClient;
    private final SpoonacularClientConfig config;
    private final ObjectMapper objectMapper;
    private final SpoonacularRecipeValidator spoonacularRecipeValidator;

    @Transactional
    public String importRecipeFromApi(String externalId, String source) {
        Long extId = Long.parseLong(externalId);

        if (apiRecipeRepository.findByExternalIdAndSource(extId, source).isPresent()) {
            log.debug("Пропуск: рецепт уже существует externalId={} source={}", extId, source);
            return "Рецепт уже существует";
        }

        String rawJson = spoonacularClient.fetchRecipeRawDataById(extId).block();
        if (rawJson == null || rawJson.isBlank()) {
            log.warn("Пустой ответ от API для externalId={}", extId);
            return "Пустой ответ от API";
        }

        SpoonacularRecipeDto dto;
        try {
            dto = objectMapper.readValue(rawJson, SpoonacularRecipeDto.class);
        } catch (Exception e) {
            log.warn("Ошибка десериализации JSON от API для externalId={}: {}", extId, e.getMessage());
            return "Ошибка JSON";
        }

        List<String> validationErrors = spoonacularRecipeValidator.validate(dto);
        if (!validationErrors.isEmpty()) {
            log.debug("Пропуск: рецепт externalId={} не прошёл валидацию. Причины: {}", extId, validationErrors);
            return "Рецепт не прошёл валидацию";
        }

        ApiRecipe recipe = ApiRecipe.builder()
                .externalId(extId)
                .source(source)
                .originalData(rawJson)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        apiRecipeRepository.save(recipe);
        log.debug("Сохранён рецепт externalId={} с id={} в БД", extId, recipe.getId());

        return recipe.getId().toString();
    }

    public void importPopularRecipes(int count) {
        int imported = 0;
        int offset = 0;

        while (imported < count) {
            try {
                String json = spoonacularClient.fetchPopularRecipeBatch(1, offset).block();
                List<Integer> ids = extractRecipeIds(json);

                if (ids.isEmpty()) {
                    log.warn("Не удалось получить рецепты на offset={}", offset);
                    break;
                }

                for (Integer id : ids) {
                    try {
                        String result = importRecipeFromApi(String.valueOf(id), config.getSource());
                        log.info("Импортирован рецепт: externalId={}, результат={}", id, result);
                        imported++;
                        if (imported >= count) break;
                        Thread.sleep(1000); // Ограничение 1 запрос/сек
                    } catch (Exception e) {
                        log.warn("Ошибка при импорте рецепта с id={}: {}", id, e.getMessage());
                    }
                }

                offset += ids.size();

            } catch (Exception e) {
                log.error("Ошибка при получении популярных рецептов: {}", e.getMessage(), e);
                break;
            }
        }
    }

    private List<Integer> extractRecipeIds(String json) {
        List<Integer> ids = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode results = root.get("results");
            if (results != null && results.isArray()) {
                for (JsonNode node : results) {
                    ids.add(node.get("id").asInt());
                }
            }
        } catch (Exception e) {
            log.error("Ошибка парсинга JSON от Spoonacular: {}", e.getMessage());
        }
        return ids;
    }
}