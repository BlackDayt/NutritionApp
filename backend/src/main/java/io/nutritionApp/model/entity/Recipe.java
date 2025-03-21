package io.nutritionApp.model.entity;


import io.nutritionApp.model.relations.RecipeIngredient;
import io.nutritionApp.model.relations.RecipeTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "recipes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Recipe {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false, unique = true)
    @ToString.Include
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeTag> recipeTags = new HashSet<>();


    @Column(nullable = false)
    private Integer calories;

    @Column(nullable = false)
    private Integer proteins;

    @Column(nullable = false)
    private Integer fats;

    @Column(nullable = false)
    private Integer carbohydrates;



    @Column(nullable = false)
    private Integer cookTime;

    @Column(nullable = false)
    private Integer servings;



    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RecipeIngredient> recipeIngredients = new HashSet<>();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @Column
    private String imageUrl;

    public Recipe(String name, String description, Integer calories, Integer proteins, Integer fats,
                  Integer carbohydrates, Integer cookTime, Integer servings, String instructions, String imageUrl) {
        this.name = name;
        this.description = description;
        this.calories = calories;
        this.proteins = proteins;
        this.fats = fats;
        this.carbohydrates = carbohydrates;
        this.cookTime = cookTime;
        this.servings = servings;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.recipeTags = new HashSet<>();
        this.recipeIngredients = new HashSet<>();
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id != null && id.equals(recipe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}