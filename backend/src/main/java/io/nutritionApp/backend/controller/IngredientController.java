package io.nutritionapp.backend.controller;

import io.nutritionapp.backend.dto.ingredient.IngredientResponse;
import io.nutritionapp.backend.model.entity.Ingredient;
import io.nutritionapp.backend.service.IngredientService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ingredients")
@Slf4j
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) {
        this.ingredientService = ingredientService;
    }

    // Получение всех ингредиентов
    @GetMapping
    public ResponseEntity<List<IngredientResponse>> getAllIngredients() {
        log.info("Запрос на получение всех ингредиентов");
        List<IngredientResponse> ingredients = ingredientService.findAll()
                .stream()
                .map(this::toIngredientResponse)
                .toList();
        return ResponseEntity.ok(ingredients);
    }

    // Получение ингредиента по ID
    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponse> getIngredientById(@PathVariable UUID id) {
        log.info("Запрос на получение ингредиента по id: {}", id);
        return ingredientService.findById(id)
                .map(this::toIngredientResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Получение ингредиента по имени
    @GetMapping("/by-name")
    public ResponseEntity<IngredientResponse> getIngredientByName(@RequestParam @NotBlank String name) {
        log.info("Запрос на получение ингредиента по имени: {}", name);
        return ingredientService.findByName(name)
                .map(this::toIngredientResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Маппер
    private IngredientResponse toIngredientResponse(Ingredient ingredient) {
        return new IngredientResponse(ingredient.getId(), ingredient.getName());
    }
}
