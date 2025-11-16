package com.microservice.microservice_user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservice.microservice_user.assembler.UserAssembler;
import com.microservice.microservice_user.dto.CreateUserDTO;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.exception.EmailAlreadyExistsException;
import com.microservice.microservice_user.exception.UserNotFoundException;
import com.microservice.microservice_user.exception.UsernameAlreadyExistsException;
import com.microservice.microservice_user.model.User;
import com.microservice.microservice_user.model.UserRole;
import com.microservice.microservice_user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAssembler userAssembler;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;
    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testUserDTO = UserDTO.builder()
                .id(testUser.getId())
                .username(testUser.getUsername())
                .email(testUser.getEmail())
                .firstName(testUser.getFirstName())
                .lastName(testUser.getLastName())
                .role(testUser.getRole())
                .active(testUser.getActive())
                .emailVerified(testUser.getEmailVerified())
                .createdAt(testUser.getCreatedAt())
                .updatedAt(testUser.getUpdatedAt())
                .build();

        createUserDTO = CreateUserDTO.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();
    }

    @Test
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnListOfUsers() {
        // Given
        List<User> users = List.of(testUser);
        List<UserDTO> userDTOs = List.of(testUserDTO);
        when(userRepository.findAll()).thenReturn(users);
        when(userAssembler.toDTOList(users)).thenReturn(userDTOs);

        // When
        List<UserDTO> result = userService.getAllUsers();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("testuser");
        verify(userRepository).findAll();
        verify(userAssembler).toDTO(testUser);
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        UUID userId = testUser.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userAssembler.toDTO(testUser)).thenReturn(testUserDTO);

        // When
        UserDTO result = userService.getUserById(userId);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId);
        verify(userRepository).findById(userId);
        verify(userAssembler).toDTO(testUser);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found")
    void getUserById_WhenUserNotExists_ShouldThrowException() {
        // Given
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Usuario no encontrado");
        
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_WhenValidData_ShouldCreateUser() {
        // Given
        when(userRepository.existsByUsername(createUserDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(false);
        when(userAssembler.fromCreateDTO(createUserDTO)).thenReturn(testUser);
        when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userAssembler.toDTO(testUser)).thenReturn(testUserDTO);

        // When
        UserDTO result = userService.createUser(createUserDTO);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
        verify(userRepository).existsByUsername(createUserDTO.getUsername());
        verify(userRepository).existsByEmail(createUserDTO.getEmail());
        verify(passwordEncoder).encode(createUserDTO.getPassword());
        verify(userRepository).save(any(User.class));
        verify(userAssembler).toDTO(testUser);
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void createUser_WhenUsernameExists_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername(createUserDTO.getUsername())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserDTO))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("ya está registrado");

        verify(userRepository).existsByUsername(createUserDTO.getUsername());
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_WhenEmailExists_ShouldThrowException() {
        // Given
        when(userRepository.existsByUsername(createUserDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(createUserDTO.getEmail())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.createUser(createUserDTO))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("ya está registrado");

        verify(userRepository).existsByUsername(createUserDTO.getUsername());
        verify(userRepository).existsByEmail(createUserDTO.getEmail());
    }

    @Test
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userAssembler.toDTO(testUser)).thenReturn(testUserDTO);

        // When
        UserDTO result = userService.getUserByUsername("testuser");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsername("testuser");
        verify(userAssembler).toDTO(testUser);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userAssembler.toDTO(testUser)).thenReturn(testUserDTO);

        // When
        UserDTO result = userService.getUserByEmail("test@example.com");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findByEmail("test@example.com");
        verify(userAssembler).toDTO(testUser);
    }
}