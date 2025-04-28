package io.nutritionapp.datapipeline.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "api_recipes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiRecipe {

    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "external_id", nullable = false)
    private Long externalId; // ID из Spoonacular

    @Column(nullable = false)
    private String source; // Например: "spoonacular"

    @Column(name = "original_data", columnDefinition = "jsonb", nullable = false)
    private String originalData; // сырой JSON из API как строка

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}