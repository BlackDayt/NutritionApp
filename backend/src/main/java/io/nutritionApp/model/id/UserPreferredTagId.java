package io.nutritionApp.model.id;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferredTagId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private UUID userId;
    private UUID tagId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreferredTagId userPreferredTagId = (UserPreferredTagId) o;
        return Objects.equals(userId, userPreferredTagId.userId) &&
                Objects.equals(tagId, userPreferredTagId.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, tagId);
    }
}
