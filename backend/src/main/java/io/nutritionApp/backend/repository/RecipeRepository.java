package io.nutritionapp.backend.repository;

import io.nutritionapp.backend.model.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    // 🔹 Найти рецепты по тегам (например, "Веганская" и "Безглютеновая")
    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.recipeTags rt WHERE rt.tag.id IN :tagIds")
    List<Recipe> findByRecipeTags(@Param("tagIds") List<UUID> tagIds);

    // 🔹 Найти рецепты, исключая определённые ингредиенты

    @Query("""
    SELECT DISTINCT r FROM Recipe r 
    WHERE r.id NOT IN (
        SELECT ri.recipe.id FROM RecipeIngredient ri 
        WHERE ri.ingredient.id IN :excludedIngredients
    )
    """)
    List<Recipe> findByExcludedIngredients(@Param("excludedIngredients") List<UUID> excludedIngredients);


    // 🔹 Найти рецепты, содержащие определённый диапазон калорий
    List<Recipe> findByCaloriesBetween(int minCalories, int maxCalories);

    // 🔹 Найти случайный рецепт (зависит от поддержки БД, в PostgreSQL работает `RANDOM()`)
//    @Query("SELECT r FROM Recipe r ORDER BY FUNCTION('RANDOM')")
    @Query(value = "SELECT * FROM recipes ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Recipe findRandomRecipe();


}

