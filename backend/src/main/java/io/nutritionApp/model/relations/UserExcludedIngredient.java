package io.nutritionApp.model.relations;

import io.nutritionApp.model.entity.Ingredient;
import io.nutritionApp.model.entity.User;
import io.nutritionApp.model.id.UserExcludedIngredientId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user_excluded_ingredients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserExcludedIngredient {
    @EmbeddedId
    private UserExcludedIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("ingredientId")
    private Ingredient ingredient;

    public static UserExcludedIngredient create(User user, Ingredient ingredient) {
        return new UserExcludedIngredient(
                new UserExcludedIngredientId(user.getId(), ingredient.getId()),
                user,
                ingredient
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserExcludedIngredient userExcludedIngredient = (UserExcludedIngredient) o;
        return id != null && id.equals(userExcludedIngredient.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
