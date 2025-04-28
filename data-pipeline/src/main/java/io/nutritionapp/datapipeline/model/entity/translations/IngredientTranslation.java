package io.nutritionapp.datapipeline.model.entity.translations;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ingredient_translations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientTranslation {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, unique = true)
    private String original;

    @Column(nullable = false)
    private String translated;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
