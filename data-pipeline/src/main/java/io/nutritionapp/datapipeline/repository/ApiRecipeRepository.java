package io.nutritionapp.datapipeline.repository;

import io.nutritionapp.datapipeline.model.entity.ApiRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiRecipeRepository extends JpaRepository<ApiRecipe, UUID> {
    Optional<ApiRecipe> findByExternalIdAndSource(Long externalId, String source);
}
