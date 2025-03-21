package io.nutritionApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealPlan implements Serializable {

    private Map<String, Double> meals = new LinkedHashMap<>();


    public Double getCaloriesForMeal(String mealName) {
        return meals.getOrDefault(mealName, 0.0);
    }


    public void addMeal(String name, Double calories) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Название приема пищи не может быть пустым");
        }
        if (calories == null || calories <= 0) {
            throw new IllegalArgumentException("Калории должны быть положительным числом");
        }
        meals.put(name, calories);
    }

    public void clear() {
        meals.clear();
    }

    public void populate(Map<String, Double> meals) {
        clear();
        meals.forEach(this::addMeal);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealPlan mealPlan)) return false;
        return Objects.equals(meals, mealPlan.meals);
    }

    @Override
    public int hashCode() {
        return Objects.hash(meals);
    }

    @Override
    public String toString() {
        return "MealPlan{" +
                "meals=" + meals +
                '}';
    }
}
