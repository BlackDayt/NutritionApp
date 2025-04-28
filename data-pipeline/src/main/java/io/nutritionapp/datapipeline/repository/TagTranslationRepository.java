package io.nutritionapp.datapipeline.repository;

import io.nutritionapp.datapipeline.model.entity.translations.TagTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TagTranslationRepository extends JpaRepository<TagTranslation, UUID> {
    Optional<TagTranslation> findByOriginalIgnoreCase(String original);
}
