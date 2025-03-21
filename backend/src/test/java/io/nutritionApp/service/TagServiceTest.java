package io.nutritionApp.service;

import io.nutritionApp.model.entity.Tag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class TagServiceTest {

    @Autowired
    private TagService tagService;

    @Test
    void shouldReturnAllTags() {
        List<Tag> tags = tagService.findAll();
        assertThat(tags).isNotEmpty();
        assertThat(tags).extracting(Tag::getName).contains("Веганская", "Безглютеновая", "Кето");
    }

    @Test
    void shouldFindTagByName() {
        Optional<Tag> tag = tagService.findByName("Веганская");
        assertThat(tag).isPresent();
        assertThat(tag.get().getName()).isEqualTo("Веганская");
    }

    @Test
    void shouldFindTagById() {
        Tag veganTag = tagService.findByName("Веганская").orElseThrow();
        Optional<Tag> foundTag = tagService.findById(veganTag.getId());
        assertThat(foundTag).isPresent();
        assertThat(foundTag.get().getName()).isEqualTo("Веганская");
    }
}
