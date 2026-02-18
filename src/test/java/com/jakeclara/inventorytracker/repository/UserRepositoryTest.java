package com.jakeclara.inventorytracker.repository;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import com.jakeclara.inventorytracker.model.User;
import com.jakeclara.inventorytracker.util.TestUserFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findByUsername returns the correct User when found")
    void findByUsername_ReturnsCorrectUser() {
        // Arrange
        User user = TestUserFactory.createDefaultUser();
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<User> foundUser = userRepository.findByUsername(user.getUsername());

        // Assert
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(user.getId());
        assertThat(foundUser.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    @DisplayName("findByUsername returns empty when user not found")
    void findByUsername_ReturnsEmptyWhenUserNotFound() {
        
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

        assertThat(foundUser).isNotPresent();
    }

    @Test
    @DisplayName("findByUsername is case-insensitive because of entity logic")
    void findByUsername_IsCaseInsensitive() {
        // Arrange
        User user = new User(
            "AdminUser",
            TestUserFactory.VALID_PASSWORD_HASH,
            TestUserFactory.VALID_ROLE
        );
        entityManager.persist(user);
        entityManager.flush();
        entityManager.clear();

        // Act
        Optional<User> foundUser = userRepository.findByUsername("adminuser");

        // Assert
        assertThat(foundUser).isPresent();
    }

    @Test 
    @DisplayName("Saving duplicate usernames throws exception")
    void save_ThrowsExceptionWhenDuplicateUsernameExists() {
        // Arrange
        userRepository.saveAndFlush(TestUserFactory.createDefaultUser());

        User duplicatUser = TestUserFactory.createDefaultUser();

        // Act & Assert
        assertThatThrownBy(() -> userRepository.saveAndFlush(duplicatUser))
            .isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }
}
