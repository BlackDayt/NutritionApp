package io.nutritionApp.repository;

import io.nutritionApp.model.entity.User;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test-postgres")
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    void shouldSaveAndFindUser() {

        Optional<User> found = userRepository.findByTelegramId(1234567890L);
        assertThat(found).isPresent();

        User user = found.get();

        assertThat(user.getTelegramId()).isEqualTo(1234567890L);

        assertThat(user.getPreferredTags())
                .isInstanceOf(Set.class)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION)
                .isNotEmpty();
        assertThat(user.getExcludedIngredients())
                .isInstanceOf(Set.class)
                .asInstanceOf(InstanceOfAssertFactories.COLLECTION)
                .isNotEmpty();
    }

    @Test
    void shouldDeleteUser() {
        Optional<User> found = userRepository.findByTelegramId(1234567890L);
        assertThat(found).isPresent();
        User user = found.get();

        userRepository.delete(user);

        Optional<User> afterDelete  = userRepository.findByTelegramId(999L);
        assertThat(afterDelete ).isEmpty();
    }
}
