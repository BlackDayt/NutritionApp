package io.nutritionApp.model.relations;

import io.nutritionApp.model.entity.Ingredient;
import io.nutritionApp.model.entity.Recipe;
import io.nutritionApp.model.id.RecipeIngredientId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "recipe_ingredients")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredient {
    @EmbeddedId
    private RecipeIngredientId id = new RecipeIngredientId();

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @MapsId("ingredientId")
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column
    private Double quantity;

    @Column
    private String unit;

    @Column
    private String quantityText;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeIngredient recipeIngredient = (RecipeIngredient) o;
        return id != null && id.equals(recipeIngredient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
