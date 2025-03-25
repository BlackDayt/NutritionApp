package io.nutritionApp.service;

import io.nutritionApp.model.entity.Ingredient;
import io.nutritionApp.model.entity.Tag;
import io.nutritionApp.model.entity.User;
import io.nutritionApp.model.relations.UserExcludedIngredient;
import io.nutritionApp.model.relations.UserPreferredTag;
import io.nutritionApp.repository.IngredientRepository;
import io.nutritionApp.repository.TagRepository;
import io.nutritionApp.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final IngredientRepository ingredientRepository;

    public UserService(UserRepository userRepository,
                       TagRepository tagRepository,
                       IngredientRepository ingredientRepository) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public Optional<User> findByTelegramId(Long telegramId) {
        log.info("Поиск пользователя с telegramId: {}", telegramId);
        return userRepository.findByTelegramId(telegramId);
    }

    public User createUser(User user, List<UUID> tagIds, List<UUID> ingredientIds) {
        log.info("Создание нового пользователя с telegramId: {}", user.getTelegramId());
        // Сохраняем самого пользователя

        // Преобразуем теги в UserPreferredTag с помощью фабричного метода
//        user.getPreferredTags().clear();
//        user.getPreferredTags().addAll(mapTags(user, tagIds));
//
//        user.getExcludedIngredients().clear();
//        user.getExcludedIngredients().addAll(mapIngredients(user, ingredientIds));


        user.setPreferredTags(mapTags(user, tagIds));
        user.setExcludedIngredients(mapIngredients(user, ingredientIds));

//        user.updateGoalCalories(); // важно!

        log.debug("Пользователь {} успешно создан с Id: {}", user.getName(), user.getId());
        return userRepository.save(user);
    }

    public User updateUser(UUID userId, User updatedUser, List<UUID> newTagIds, List<UUID> newIngredientIds) {
        log.info("Обновление пользователя с Id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Обновляем основные поля пользователя

        user.setName(updatedUser.getName());
        user.setAge(updatedUser.getAge());
        user.setWeight(updatedUser.getWeight());
        user.setHeight(updatedUser.getHeight());
        user.setGender(updatedUser.getGender());
        user.setActivityLevel(updatedUser.getActivityLevel());
        user.setDietGoal(updatedUser.getDietGoal());
        user.setMealCount(updatedUser.getMealCount());

        // Обновляем связи

        user.getPreferredTags().clear();
        user.getPreferredTags().addAll(mapTags(user, newTagIds));

        user.getExcludedIngredients().clear();
        user.getExcludedIngredients().addAll(mapIngredients(user, newIngredientIds));

        log.debug("Пользователь {} обновлен", user.getId());
        return userRepository.save(user);
    }



    private Set<UserPreferredTag> mapTags(User user, List<UUID> tagIds) {
        log.debug("Привязка тегов к пользователю {}", user.getId());
        return tagIds.stream()
                .map(tagId -> {
                    Tag tag = tagRepository.findById(tagId)
                            .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + tagId));
                    return UserPreferredTag.create(user, tag);
                })
                .collect(Collectors.toSet());
    }

    private Set<UserExcludedIngredient> mapIngredients(User user, List<UUID> ingredientIds) {
        log.debug("Привязка исключенных ингредиентов к пользователю {}", user.getId());
        return ingredientIds.stream()
                .map(ingredientId -> {
                    Ingredient ingredient = ingredientRepository.findById(ingredientId)
                            .orElseThrow(() -> new EntityNotFoundException("Ingredient not found: " + ingredientId));
                    return UserExcludedIngredient.create(user, ingredient);
                })
                .collect(Collectors.toSet());
    }

    public void deleteById(UUID id) {
        log.warn("Удаление пользователя с Id: {}", id);
        userRepository.deleteById(id);
    }

    public List<User> findAll() {
        log.debug("Получение списка всех пользователей");
        return userRepository.findAll();
    }

}