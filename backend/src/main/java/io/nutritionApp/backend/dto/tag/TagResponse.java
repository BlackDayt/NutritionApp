package io.nutritionapp.backend.dto.tag;

import java.util.UUID;

public record TagResponse(
        UUID id,
        String name
) {
}
