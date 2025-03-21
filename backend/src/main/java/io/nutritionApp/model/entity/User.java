package io.nutritionApp.model.entity;

import io.nutritionApp.model.MealPlan;
import io.nutritionApp.model.enums.ActivityLevel;
import io.nutritionApp.model.enums.DietGoal;
import io.nutritionApp.model.enums.Gender;
import io.nutritionApp.model.relations.UserExcludedIngredient;
import io.nutritionApp.model.relations.UserPreferredTag;
import io.nutritionApp.converter.MealPlanConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "UUID")
    private UUID id; // Telegram ID пользователя (или UUID?)

    @Column(name = "telegram_id", unique = true, nullable = false)
    private Long telegramId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    @Min(1)
    private Integer age;

    @Column(nullable = false)
    @Positive
    private double weight;

    @Column(nullable = false)
    @Positive
    private double height;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private ActivityLevel activityLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private DietGoal dietGoal;

    @Column(nullable = false)
    private double goalCalories;

    @Column(nullable = false)
    private int mealCount;

    @Convert(converter = MealPlanConverter.class)
    @Column(name = "meal_plan", columnDefinition = "jsonb")
    private MealPlan mealPlan;  // Храним рацион в виде JSON


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserPreferredTag> preferredTags = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserExcludedIngredient> excludedIngredients = new HashSet<>();


    public double calculateBMR() {
        return gender == Gender.MALE
                ? (10 * weight + 6.25 * height - 5 * age + 5)
                : (10 * weight + 6.25 * height - 5 * age - 161);
    }

    public double calculateTDEE() {
        return calculateBMR() * activityLevel.getMultiplier();
    }

    public double calculateGoalCalories() {
        return calculateTDEE() * dietGoal.getFactor();
    }

    public void updateGoalCalories() {
        this.goalCalories = calculateGoalCalories();
        updateMealPlan();
    }

    // 🔹 Генерируем план питания
    public void updateMealPlan() {
        Map<String, Double> meals = generateMeals();
        if (mealPlan == null) {
            mealPlan = new MealPlan();
        }
        mealPlan.setMeals(meals);
    }

    public Map<String, Double> generateMeals() {
        Map<String, Double> meals = new LinkedHashMap<>();

        String[] mealNames;
        double[] ratios = switch (mealCount) {
            case 3 -> {
                mealNames = new String[]{"Завтрак", "Обед", "Ужин"};
                yield new double[]{0.3, 0.4, 0.3};
            }
            case 4 -> {
                mealNames = new String[]{"Завтрак", "Обед", "Перекус", "Ужин"};
                yield new double[]{0.25, 0.35, 0.15, 0.25};
            }
            case 5 -> {
                mealNames = new String[]{"Завтрак", "Второй завтрак", "Обед", "Полдник", "Ужин"};
                yield new double[]{0.2, 0.15, 0.3, 0.15, 0.2};
            }
            default -> throw new IllegalArgumentException("Некорректное количество приемов пищи");
        };

        for (int i = 0; i < mealNames.length; i++) {
            meals.put(mealNames[i], goalCalories * ratios[i]);
        }

        return meals;
    }


    public MealPlan getMealPlan() {
        return mealPlan != null ? mealPlan : new MealPlan();
    }

    // 🔹 Устанавливаем mealPlanJson безопасно
    public void setMealPlan(MealPlan mealPlan) {
        if (mealPlan == null || mealPlan.getMeals().isEmpty()) {
            throw new IllegalArgumentException("План питания не может быть пустым");
        }
        this.mealPlan = mealPlan;
    }

    // Setters
    public void setWeight(double weight) {
        if (weight <= 0) throw new IllegalArgumentException("Вес должен быть больше 0");
        this.weight = weight;
    }

    public void setHeight(double height) {
        if (height <= 0) throw new IllegalArgumentException("Рост должен быть больше 0");
        this.height = height;
    }

    public void setAge(Integer age) {
        if (age == null || age < 1) throw new IllegalArgumentException("Возраст должен быть минимум 1 год");
        this.age = age;
    }
    public void setActivityLevel(ActivityLevel activityLevel) {
        if (activityLevel == null) throw new IllegalArgumentException("Уровень активности не может быть null");
        this.activityLevel = activityLevel;
    }

    public void setDietGoal(DietGoal dietGoal) {
        if (dietGoal == null) throw new IllegalArgumentException("Цель диеты не может быть null");
        this.dietGoal = dietGoal;
    }

}