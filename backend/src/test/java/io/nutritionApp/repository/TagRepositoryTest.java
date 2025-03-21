package io.nutritionApp.repository;

import io.nutritionApp.model.entity.Tag;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Test
    void shouldFindTagByName() {
        Optional<Tag> tagOpt = tagRepository.findByName("Веганская");

        assertThat(tagOpt).isPresent();
        assertThat(tagOpt.get().getName()).isEqualTo("Веганская");
    }

    @Test
    void shouldReturnEmptyIfTagNotFound() {
        Optional<Tag> tagOpt = tagRepository.findByName("Несуществующий тег");

        assertThat(tagOpt).isEmpty();
    }
}