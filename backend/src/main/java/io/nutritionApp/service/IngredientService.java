package io.nutritionApp.service;

import io.nutritionApp.model.entity.Ingredient;
import io.nutritionApp.repository.IngredientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class IngredientService {
    private static final Logger log = LoggerFactory.getLogger(IngredientService.class);

    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    public List<Ingredient> findAll() {
        log.debug("Получение всех ингредиентов");
        return ingredientRepository.findAll();
    }

    public Optional<Ingredient> findById(UUID id) {
        log.debug("Поиск ингредиента: {}", id);
        return ingredientRepository.findById(id);
    }

    public Optional<Ingredient> findByName(String name) {
        log.debug("Поиск ингредиента: {}", name);
        return ingredientRepository.findByName(name);
    }

}
