package io.nutritionapp.datapipeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.datapipeline.client.GoogleTranslateClient;
import io.nutritionapp.datapipeline.dto.RecipeDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularRecipeDto;
import io.nutritionapp.datapipeline.dto.spoonacular.SpoonacularIngredientDto;
import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import io.nutritionapp.datapipeline.model.entity.translations.IngredientTranslation;
import io.nutritionapp.datapipeline.model.entity.translations.TagTranslation;
import io.nutritionapp.datapipeline.repository.IngredientTranslationRepository;
import io.nutritionapp.datapipeline.repository.TagTranslationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationService {

    private final TagTranslationRepository tagTranslationRepository;
    private final IngredientTranslationRepository ingredientTranslationRepository;
    private final GoogleTranslateClient googleTranslateClient;

    public RecipeDto translateRecipe(ApiRecipe recipe) {
        try {
            SpoonacularRecipeDto dto = new ObjectMapper().readValue(recipe.getOriginalData(), SpoonacularRecipeDto.class);

            List<String> translatedTags = Stream.concat(
                            dto.getDishTypes() != null ? dto.getDishTypes().stream() : Stream.empty(),
                            dto.getCuisines() != null ? dto.getCuisines().stream() : Stream.empty()
                    )
                    .filter(tag -> tag != null && !tag.isBlank())
                    .map(this::translateTag)
                    .distinct()
                    .collect(Collectors.toList());

            List<String> translatedIngredients = dto.getExtendedIngredients() != null
                    ? dto.getExtendedIngredients().stream()
                    .map(SpoonacularIngredientDto::getOriginal)
                    .filter(i -> i != null && !i.isBlank())
                    .map(this::translateIngredient)
                    .collect(Collectors.toList())
                    : List.of();

            return RecipeDto.builder()
                    .externalId(recipe.getExternalId())
                    .name(dto.getTitle())
                    .description(dto.getSummary())
                    .instructions(dto.getInstructions())
                    .imageUrl(dto.getImage())
                    .tags(translatedTags)
                    .ingredients(translatedIngredients)
                    .calories(dto.getNutrition().findValueByName("Calories"))
                    .proteins(dto.getNutrition().findValueByName("Protein"))
                    .fats(dto.getNutrition().findValueByName("Fat"))
                    .carbohydrates(dto.getNutrition().findValueByName("Carbohydrates"))
                    .cookTime(dto.getReadyInMinutes())
                    .servings(dto.getServings())
                    .build();

        } catch (Exception e) {
            log.error("Ошибка при парсинге или переводе рецепта externalId={}: {}", recipe.getExternalId(), e.getMessage(), e);
            throw new RuntimeException("Не удалось перевести рецепт", e);
        }

    }

    public String translateTag(String original) {
        return tagTranslationRepository.findByOriginalIgnoreCase(original)
                .map(TagTranslation::getTranslated)
                .orElseGet(() -> {
                    String translated = googleTranslateClient.translate(original);
                    log.debug("Новый перевод тега '{}' -> '{}'", original, translated);

                    TagTranslation newTranslation = TagTranslation.builder()
                            .original(original)
                            .translated(translated)
                            .createdAt(LocalDateTime.now())
                            .build();
                    tagTranslationRepository.save(newTranslation);
                    return translated;
                });
    }

    public String translateIngredient(String original) {
        return ingredientTranslationRepository.findByOriginalIgnoreCase(original)
                .map(IngredientTranslation::getTranslated)
                .orElseGet(() -> {
                    String translated = googleTranslateClient.translate(original);
                    log.debug("Новый перевод ингредиента '{}' -> '{}'", original, translated);

                    IngredientTranslation newTranslation = IngredientTranslation.builder()
                            .original(original)
                            .translated(translated)
                            .createdAt(LocalDateTime.now())
                            .build();
                    ingredientTranslationRepository.save(newTranslation);
                    return translated;
                });
    }
}
