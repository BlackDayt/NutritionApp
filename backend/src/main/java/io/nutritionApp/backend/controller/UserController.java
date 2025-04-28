package io.nutritionapp.backend.controller;

import io.nutritionapp.backend.dto.user.CreateUserRequest;
import io.nutritionapp.backend.dto.user.UpdateUserRequest;
import io.nutritionapp.backend.dto.user.UserResponse;
import io.nutritionapp.backend.mapper.UserMapper;
import io.nutritionapp.backend.model.entity.User;
import io.nutritionapp.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    // Получение пользователя по telegramId
    @GetMapping("/telegram/{telegramId}")
    public ResponseEntity<UserResponse> getUserByTelegramId(@PathVariable Long telegramId) {
        log.info("Запрос на получение пользователя по telegramId: {}", telegramId);
        return userService.findByTelegramId(telegramId)
                .map(userMapper::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Создание пользователя
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        log.info("Запрос на создание пользователя с telegramId: {}", request.getTelegramId());
        User user = userMapper.toUser(request);
        User createdUser = userService.createUser(user, request.getPreferredTagIds(), request.getExcludedIngredientIds());
        return ResponseEntity.ok(userMapper.toUserResponse(createdUser));
    }

    // Обновление пользователя
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        log.info("Запрос на обновление пользователя с id: {}", id);
        User user = userService.findByTelegramId(request.getTelegramId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с telegramId " + request.getTelegramId() + " не найден"));

        userMapper.updateUserFromDto(user, request);
        User updatedUser = userService.updateUser(id, user, request.getPreferredTagIds(), request.getExcludedIngredientIds());
        return ResponseEntity.ok(userMapper.toUserResponse(updatedUser));
    }

    // Удаление пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Запрос на удаление пользователя с id: {}", id);
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Получение всех пользователей
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Запрос на получение всех пользователей");
        List<UserResponse> users = userService.findAll()
                .stream()
                .map(userMapper::toUserResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

}