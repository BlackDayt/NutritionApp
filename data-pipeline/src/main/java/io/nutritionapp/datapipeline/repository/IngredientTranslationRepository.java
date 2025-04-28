package io.nutritionapp.datapipeline.repository;

import io.nutritionapp.datapipeline.model.entity.translations.IngredientTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IngredientTranslationRepository extends JpaRepository<IngredientTranslation, UUID> {
    Optional<IngredientTranslation> findByOriginalIgnoreCase(String original);
}
