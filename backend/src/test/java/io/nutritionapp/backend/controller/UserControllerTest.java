package io.nutritionapp.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.nutritionapp.backend.dto.user.CreateUserRequest;
import io.nutritionapp.backend.dto.user.UpdateUserRequest;
import io.nutritionapp.backend.model.enums.ActivityLevel;
import io.nutritionapp.backend.model.enums.DietGoal;
import io.nutritionapp.backend.model.enums.Gender;
import io.nutritionapp.backend.repository.IngredientRepository;
import io.nutritionapp.backend.repository.TagRepository;
import io.nutritionapp.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-postgres")
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnUserByTelegramId() throws Exception {
        mockMvc.perform(get("/api/users/telegram/{telegramId}", 1234567890L))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.telegramId").value(1234567890L))
                .andExpect(jsonPath("$.name").value("Тестовый пользователь"))
                .andExpect(jsonPath("$.mealPlan").exists());
    }

    @Test
    void shouldReturn404IfUserNotFound() throws Exception {
        mockMvc.perform(get("/api/users/telegram/{telegramId}", 9999999999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreateUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest(
                1122334455L,
                "Пользователь API",
                Gender.FEMALE,
                28,
                60,
                165,
                ActivityLevel.MODERATE,
                DietGoal.CUTTING,
                4,
                List.of(tagRepository.findByName("Веганская").get().getId()),
                List.of(ingredientRepository.findByName("Глютен").get().getId())
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.telegramId").value(1122334455L))
                .andExpect(jsonPath("$.preferredTagIds").isArray())
                .andExpect(jsonPath("$.excludedIngredientIds").isArray())
        ;
    }

    @Test
    void shouldUpdateUser() throws Exception {
        UUID userId = userRepository.findByTelegramId(1234567890L).orElseThrow().getId();

        UpdateUserRequest request = new UpdateUserRequest(
                1234567890L,
                "Обновленный пользователь",
                Gender.MALE,
                35,
                80,
                185,
                ActivityLevel.ACTIVE,
                DietGoal.MAINTAIN,
                5,
                List.of(tagRepository.findByName("Кето-диета").get().getId()),
                List.of()
        );

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Обновленный пользователь"))
                .andExpect(jsonPath("$.mealCount").value(5))
                .andExpect(jsonPath("$.goalCalories").exists());
    }

    @Test
    void shouldDeleteUser() throws Exception {
        UUID userId = userRepository.findByTelegramId(1234567890L).orElseThrow().getId();

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }
}
