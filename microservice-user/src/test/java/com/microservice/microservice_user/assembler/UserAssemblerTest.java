package com.microservice.microservice_user.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.microservice.microservice_user.dto.CreateUserDTO;
import com.microservice.microservice_user.dto.UpdateUserDTO;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserRole;

@DisplayName("User Assembler Tests")
class UserAssemblerTest {

    private UserAssembler userAssembler;
    private User testUser;

    @BeforeEach
    void setUp() {
        userAssembler = new UserAssembler();
        
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123456789")
                .profileImageUrl("http://example.com/image.jpg")
                .role(UserRole.USER)
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should convert User entity to UserDTO")
    void toDTO_ShouldConvertUserToDTO() {
        // When
        UserDTO result = userAssembler.toDTO(testUser);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testUser.getId());
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(result.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(result.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(result.getPhoneNumber()).isEqualTo(testUser.getPhoneNumber());
        assertThat(result.getProfileImageUrl()).isEqualTo(testUser.getProfileImageUrl());
        assertThat(result.getRole()).isEqualTo(testUser.getRole());
        assertThat(result.getActive()).isEqualTo(testUser.getActive());
        assertThat(result.getEmailVerified()).isEqualTo(testUser.getEmailVerified());
        assertThat(result.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
        assertThat(result.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt());
    }

    @Test
    @DisplayName("Should return null when User entity is null")
    void toDTO_WithNullUser_ShouldReturnNull() {
        // When
        UserDTO result = userAssembler.toDTO(null);

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Should convert CreateUserDTO to User entity")
    void toEntity_ShouldConvertCreateUserDTOToUser() {
        // Given
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .phoneNumber("987654321")
                .build();

        // When
        User result = userAssembler.toEntity(createUserDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(createUserDTO.getUsername());
        assertThat(result.getEmail()).isEqualTo(createUserDTO.getEmail());
        assertThat(result.getPassword()).isEqualTo(createUserDTO.getPassword());
        assertThat(result.getFirstName()).isEqualTo(createUserDTO.getFirstName());
        assertThat(result.getLastName()).isEqualTo(createUserDTO.getLastName());
        assertThat(result.getPhoneNumber()).isEqualTo(createUserDTO.getPhoneNumber());
        assertThat(result.getRole()).isEqualTo(UserRole.USER);
        assertThat(result.getActive()).isTrue();
        assertThat(result.getEmailVerified()).isFalse();
    }

    @Test
    @DisplayName("Should update User entity from UpdateUserDTO")
    void updateEntity_ShouldUpdateUserFromDTO() {
        // Given
        UpdateUserDTO updateUserDTO = UpdateUserDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .phoneNumber("555123456")
                .profileImageUrl("http://example.com/new-image.jpg")
                .build();

        User userToUpdate = User.builder()
                .id(testUser.getId())
                .username(testUser.getUsername())
                .email(testUser.getEmail())
                .password(testUser.getPassword())
                .firstName(testUser.getFirstName())
                .lastName(testUser.getLastName())
                .phoneNumber(testUser.getPhoneNumber())
                .profileImageUrl(testUser.getProfileImageUrl())
                .role(testUser.getRole())
                .active(testUser.getActive())
                .emailVerified(testUser.getEmailVerified())
                .createdAt(testUser.getCreatedAt())
                .updatedAt(testUser.getUpdatedAt())
                .build();

        // When
        userAssembler.updateEntity(userToUpdate, updateUserDTO);

        // Then
        assertThat(userToUpdate.getFirstName()).isEqualTo(updateUserDTO.getFirstName());
        assertThat(userToUpdate.getLastName()).isEqualTo(updateUserDTO.getLastName());
        assertThat(userToUpdate.getPhoneNumber()).isEqualTo(updateUserDTO.getPhoneNumber());
        assertThat(userToUpdate.getProfileImageUrl()).isEqualTo(updateUserDTO.getProfileImageUrl());
        
        // Verify unchanged fields
        assertThat(userToUpdate.getId()).isEqualTo(testUser.getId());
        assertThat(userToUpdate.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(userToUpdate.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(userToUpdate.getPassword()).isEqualTo(testUser.getPassword());
        assertThat(userToUpdate.getRole()).isEqualTo(testUser.getRole());
    }
}