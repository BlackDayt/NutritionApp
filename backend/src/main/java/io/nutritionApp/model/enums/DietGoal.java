package io.nutritionApp.model.enums;

import lombok.Getter;

@Getter
public enum DietGoal {
    MAINTAIN(1.0),              // Поддержание веса
    WEIGHT_LOSS(0.85),          // Похудение (-15%)
    EXTREME_WEIGHT_LOSS(0.75),  // Экстремальное похудение (-25%)
    MUSCLE_GAIN(1.10),          // Набор массы (+10%)
    BULK(1.20),                 // Интенсивный набор массы (+20%)
    CUTTING(0.80);              // Сушка (-20%)

    private final double factor;

    DietGoal(double factor) {
        this.factor = factor;
    }

}
