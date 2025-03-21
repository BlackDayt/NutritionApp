package io.nutritionApp.model.relations;

import io.nutritionApp.model.entity.Tag;
import io.nutritionApp.model.entity.User;
import io.nutritionApp.model.id.UserPreferredTagId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "user_preferred_tags")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferredTag {
    @EmbeddedId
    private UserPreferredTagId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId")
    private Tag tag;


    public static UserPreferredTag create(User user, Tag tag) {
        return new UserPreferredTag(
                new UserPreferredTagId(user.getId(), tag.getId()),
                user,
                tag
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPreferredTag userPreferredTag = (UserPreferredTag) o;
        return id != null && id.equals(userPreferredTag.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
