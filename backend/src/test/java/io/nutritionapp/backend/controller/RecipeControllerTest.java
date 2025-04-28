package io.nutritionapp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.backend.repository.IngredientRepository;
import io.nutritionapp.backend.repository.TagRepository;
import io.nutritionapp.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-postgres")
@Transactional
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnRandomRecipeForUser() throws Exception {
        Long telegramId = 1234567890L;
        mockMvc.perform(get("/api/recipes/random").param("telegramId", telegramId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.calories").exists());
    }

    @Test
    void shouldFindRecipesByTags() throws Exception {
        List<UUID> tagIds = List.of(
                tagRepository.findByName("Веганская").get().getId()
        );

        mockMvc.perform(get("/api/recipes/by-tags")
                        .param("tagIds", tagIds.get(0).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void shouldFindRecipesByExcludedIngredients() throws Exception {
        List<UUID> ingredientIds = List.of(
                ingredientRepository.findByName("Глютен").get().getId()
        );

        mockMvc.perform(get("/api/recipes/by-excluded-ingredients")
                        .param("excludedIngredientIds", ingredientIds.get(0).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void shouldFindRecipesByCalories() throws Exception {
        mockMvc.perform(get("/api/recipes/by-calories")
                        .param("minCalories", "100")
                        .param("maxCalories", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].calories").value(org.hamcrest.Matchers.lessThanOrEqualTo(1000)));
    }
}
