package io.nutritionApp.model.enums;

import lombok.Getter;

@Getter
public enum ActivityLevel {
    SEDENTARY(1.2),       // Малоподвижный образ жизни
    LIGHT(1.375),         // Лёгкие тренировки 1-3 раза в неделю
    MODERATE(1.55),       // Тренировки 3-5 раз в неделю
    ACTIVE(1.725),        // Тренировки 5-7 раз в неделю
    VERY_ACTIVE(1.9);     // Интенсивные тренировки каждый день

    private final double multiplier;

    ActivityLevel(double multiplier) {
        this.multiplier = multiplier;
    }

}
