package io.nutritionapp.datapipeline.service;

import io.nutritionapp.datapipeline.dto.RecipeDto;
import io.nutritionapp.datapipeline.external.GoogleAccessTokenProvider;
import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import io.nutritionapp.datapipeline.repository.ApiRecipeRepository;
import io.nutritionapp.datapipeline.util.ApiRecipeToRecipeDtoMapper;
import io.nutritionapp.datapipeline.util.RecipeDtoValidator;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.*;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {
    @Mock private ApiRecipeRepository apiRecipeRepository;
    @Mock private ApiRecipeToRecipeDtoMapper mapper;
    @Mock private GoogleAccessTokenProvider tokenProvider;
    @Mock private TranslationService translationService;
    @Mock private RecipeDtoValidator recipeDtoValidator;


    private ExportService exportService;
    private static MockWebServer mockWebServer;
    private ApiRecipe recipe;
    private RecipeDto dto;

    @BeforeAll
    static void startServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopServer() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() {
        recipe = ApiRecipe.builder()
                .id(UUID.randomUUID())
                .externalId(123L)
                .source("spoonacular")
                .originalData("{}")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        dto = RecipeDto.builder()
                .externalId(123L)
                .name("Test Recipe")
                .description("desc")
                .instructions("cook")
                .imageUrl("http://image")
                .tags(List.of("vegan"))
                .ingredients(List.of("tomato"))
                .calories(100)
                .proteins(10)
                .fats(5)
                .carbohydrates(15)
                .cookTime(30)
                .servings(2)
                .build();

        WebClient realClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .defaultHeader("Authorization", "Bearer fake-token")
                .build();

        exportService = new ExportService(
                realClient, // теперь передаём вручную
                apiRecipeRepository,
                mapper,
                tokenProvider,
                translationService,
                recipeDtoValidator
        );


//        ReflectionTestUtils.setField(exportService, "webClient", realClient);
        ReflectionTestUtils.setField(exportService, "spreadsheetId", "test-id");
        ReflectionTestUtils.setField(exportService, "sheetName", "Sheet1");
    }

    @Test
    void shouldExportValidRecipes() throws InterruptedException {
        when(apiRecipeRepository.findAll()).thenReturn(List.of(recipe));
        when(translationService.translateRecipe(recipe)).thenReturn(dto);
        when(recipeDtoValidator.validateAndReport(dto)).thenReturn(Collections.emptyList());
        when(tokenProvider.getAccessToken()).thenReturn("fake-token");

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("ok"));

        assertDoesNotThrow(() -> exportService.exportAllRecipesToGoogleSheet());

        var recordedRequest = mockWebServer.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertTrue(recordedRequest.getPath().contains("test-id"));
        assertTrue(recordedRequest.getBody().readUtf8().contains("Test Recipe"));
    }

    @Test
    void shouldNotExportWhenNoRecipes() {
        when(apiRecipeRepository.findAll()).thenReturn(Collections.emptyList());

        exportService.exportAllRecipesToGoogleSheet();

        verify(apiRecipeRepository).findAll();
//        verifyNoInteractions(translationService, recipeDtoValidator, tokenProvider, webClient);
        verifyNoInteractions(translationService, recipeDtoValidator, tokenProvider);
    }

    @Test
    void shouldSkipInvalidRecipes() {
        when(apiRecipeRepository.findAll()).thenReturn(List.of(recipe));
        when(translationService.translateRecipe(recipe)).thenReturn(dto);
        when(recipeDtoValidator.validateAndReport(dto)).thenReturn(List.of("Invalid"));

        exportService.exportAllRecipesToGoogleSheet();

        verify(recipeDtoValidator).validateAndReport(dto);
        verifyNoInteractions(tokenProvider);
    }

    @Test
    void shouldHandleTranslationException() {
        when(apiRecipeRepository.findAll()).thenReturn(List.of(recipe));
        when(translationService.translateRecipe(recipe)).thenThrow(new RuntimeException("translation failed"));

        assertDoesNotThrow(() -> exportService.exportAllRecipesToGoogleSheet());
    }
}