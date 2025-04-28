package io.nutritionapp.datapipeline.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.datapipeline.config.SpoonacularClientConfig;
import io.nutritionapp.datapipeline.client.SpoonacularClient;
import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import io.nutritionapp.datapipeline.repository.ApiRecipeRepository;
import io.nutritionapp.datapipeline.util.SpoonacularRecipeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ImportServiceTest {

    @Mock
    private ApiRecipeRepository apiRecipeRepository;

    @Mock
    private SpoonacularClient spoonacularClient;

    @Mock
    private SpoonacularClientConfig config;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SpoonacularRecipeValidator spoonacularRecipeValidator;

    @InjectMocks
    private ImportService importService;

    private final String rawJson = "{\"title\":\"Test Recipe\"}";
    private final String source = "spoonacular";

    @BeforeEach
    void setUp() {
        lenient().when(spoonacularClient.fetchRecipeRawDataById(123L)).thenReturn(Mono.just(rawJson));
        lenient().when(spoonacularRecipeValidator.validate(any())).thenReturn(java.util.Collections.emptyList());
    }

    @Test
    void shouldImportRecipeIfNotExists() {
        when(apiRecipeRepository.findByExternalIdAndSource(123L, source)).thenReturn(Optional.empty());
        when(apiRecipeRepository.save(any())).thenAnswer(invocation -> {
            ApiRecipe recipe = invocation.getArgument(0);
            recipe.setId(UUID.randomUUID());
            return recipe;
        });

        String id = importService.importRecipeFromApi("123", source);

        assertNotNull(id);
        verify(apiRecipeRepository).save(any(ApiRecipe.class));
    }

    @Test
    void shouldNotImportIfAlreadyExists() {
        ApiRecipe existing = ApiRecipe.builder()
                .id(UUID.randomUUID())
                .externalId(123L)
                .source(source)
                .originalData(rawJson)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(apiRecipeRepository.findByExternalIdAndSource(123L, source)).thenReturn(Optional.of(existing));

        String result = importService.importRecipeFromApi("123", source);

        assertTrue(result.contains("уже существует"));
        verify(apiRecipeRepository, never()).save(any());
    }
}
