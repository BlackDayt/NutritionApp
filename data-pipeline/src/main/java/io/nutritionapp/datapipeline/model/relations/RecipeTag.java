package io.nutritionapp.datapipeline.model.relations;

import io.nutritionapp.datapipeline.model.entity.Recipe;
import io.nutritionapp.datapipeline.model.id.RecipeTagId;
import io.nutritionapp.datapipeline.model.entity.Tag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "recipe_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeTag {
    @EmbeddedId
    private RecipeTagId id = new RecipeTagId();

    @ManyToOne
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(nullable = false)
    private Integer priority;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecipeTag recipeTag = (RecipeTag) o;
        return id != null && id.equals(recipeTag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
