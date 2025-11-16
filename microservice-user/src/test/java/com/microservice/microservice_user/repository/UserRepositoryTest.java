package com.microservice.microservice_user.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserRole;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .active(true)
                .emailVerified(false)
                .build();
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByUsername("testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should find user by email")
    void findByEmail_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should find user by username or email")
    void findByUsernameOrEmail_WithUsername_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByUsernameOrEmail("testuser", "testuser");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Should find user by username or email with email")
    void findByUsernameOrEmail_WithEmail_ShouldReturnUser() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        Optional<User> result = userRepository.findByUsernameOrEmail("test@example.com", "test@example.com");

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_ShouldReturnTrue() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepository.existsByUsername("testuser");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should check if username does not exist")
    void existsByUsername_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistentuser");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_ShouldReturnTrue() {
        // Given
        entityManager.persistAndFlush(testUser);

        // When
        boolean exists = userRepository.existsByEmail("test@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should check if email does not exist")
    void existsByEmail_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should return empty when username not found")
    void findByUsername_WhenNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByUsername("nonexistentuser");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should return empty when email not found")
    void findByEmail_WhenNotExists_ShouldReturnEmpty() {
        // When
        Optional<User> result = userRepository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(result).isEmpty();
    }
}