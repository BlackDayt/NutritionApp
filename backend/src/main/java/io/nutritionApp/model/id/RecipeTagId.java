package io.nutritionApp.model.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeTagId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID recipeId;
    private UUID tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeTagId recipeTagId = (RecipeTagId) o;
        return Objects.equals(recipeId, recipeTagId.recipeId) &&
                Objects.equals(tagId, recipeTagId.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recipeId, tagId);
    }
}
