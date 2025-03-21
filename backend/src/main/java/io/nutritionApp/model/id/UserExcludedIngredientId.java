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
public class UserExcludedIngredientId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private UUID ingredientId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserExcludedIngredientId userExcludedIngredientId = (UserExcludedIngredientId) o;
        return Objects.equals(userId, userExcludedIngredientId.userId) &&
                Objects.equals(ingredientId, userExcludedIngredientId.ingredientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, ingredientId);
    }
}
