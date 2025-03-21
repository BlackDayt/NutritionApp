package io.nutritionApp.service;

import io.nutritionApp.model.entity.Ingredient;
import io.nutritionApp.model.entity.Tag;
import io.nutritionApp.model.entity.User;
import io.nutritionApp.repository.IngredientRepository;
import io.nutritionApp.repository.TagRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private IngredientRepository ingredientRepository;


    @Test
    void shouldCreateUserWithRelations() {
        // Подготовка данных
        User user = userService.findByTelegramId(1234567890L).orElseThrow();

        assertThat(user.getId()).isNotNull();
        assertThat(user.getPreferredTags()).hasSize(2);
        assertThat(user.getExcludedIngredients()).hasSize(1);
        assertThat(user.getGoalCalories()).isGreaterThan(0);

        assertThat(user.getMealPlan().getMeals()).hasSize(3);
        assertThat(user.getMealPlan().getMeals()).containsKeys("Завтрак", "Обед", "Ужин");
    }

    @Test
    void shouldUpdateUserDataAndRelations() {
        // Данные для обновления
        User user = userService.findByTelegramId(1234567890L).orElseThrow();

        Tag newTag = tagRepository.findByName("Безглютеновая").orElseThrow();
        Ingredient newIngredient = ingredientRepository.findByName("Тофу").orElseThrow();

        user.setMealCount(5);
        user.setWeight(80);
        user.setHeight(185);

        User updatedUser = userService.updateUser(user.getId(), user, List.of(newTag.getId()), List.of(newIngredient.getId()));

        // Проверяем поля
        assertThat(updatedUser.getMealCount()).isEqualTo(5);
        assertThat(updatedUser.getWeight()).isEqualTo(80);
        assertThat(updatedUser.getHeight()).isEqualTo(185);

        // Проверяем обновленные связи
        assertThat(updatedUser.getPreferredTags())
                .extracting(tag -> tag.getTag().getName())
                .containsExactly("Безглютеновая");

        assertThat(updatedUser.getExcludedIngredients())
                .extracting(ingredient -> ingredient.getIngredient().getName())
                .containsExactly("Тофу");
    }

    @Test
    void shouldDeleteUser() {
        // Загружаем пользователя из базы
        User user = userService.findByTelegramId(1234567890L).orElseThrow();

        // Удаляем пользователя
        userService.deleteById(user.getId());


        Optional<User> deletedUser = userService.findByTelegramId(user.getTelegramId());
        assertThat(deletedUser).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenTagNotFound() {
        User user = userService.findByTelegramId(1234567890L).orElseThrow();
        UUID nonExistentTagId = UUID.randomUUID();
        List<UUID> tags = List.of(nonExistentTagId);
        List<UUID> ingredients = List.of();

        assertThatThrownBy(() -> userService.createUser(user, tags, ingredients))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Tag not found");
    }

    @Test
    void shouldThrowExceptionWhenIngredientNotFound() {
        User user = userService.findByTelegramId(1234567890L).orElseThrow();
        UUID nonExistentIngredientId = UUID.randomUUID();
        List<UUID> tags = List.of();
        List<UUID> ingredients = List.of(nonExistentIngredientId);

        assertThatThrownBy(() -> userService.createUser(user, tags, ingredients))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Ingredient not found");
    }
}
