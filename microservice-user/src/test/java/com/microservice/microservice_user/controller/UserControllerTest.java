package com.microservice.microservice_user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.microservice_user.config.SecurityConfig;
import com.microservice.microservice_user.dto.CreateUserDTO;
import com.microservice.microservice_user.dto.UserDTO;
import com.microservice.microservice_user.model.UserRole;
import com.microservice.microservice_user.service.UserService;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = UserDTO.builder()
                .id(UUID.randomUUID())
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .role(UserRole.USER)
                .active(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        // Given
        List<UserDTO> users = List.of(testUserDTO);
        when(userService.getAllUsers()).thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("testuser"))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get user by ID successfully")
    void getUserById_ShouldReturnUser() throws Exception {
        // Given
        UUID userId = testUserDTO.getId();
        when(userService.getUserById(userId)).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        when(userService.createUser(any(CreateUserDTO.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("Should return bad request for invalid user data")
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given - CreateUserDTO with invalid data
        CreateUserDTO invalidUser = CreateUserDTO.builder()
                .username("") // Invalid: empty username
                .email("invalid-email") // Invalid: bad email format
                .password("123") // Invalid: too short
                .build();

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserByUsername("testuser")).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/username/{username}", "testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    @DisplayName("Should get user by email successfully")
    void getUserByEmail_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserByEmail("test@example.com")).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/email/{email}", "test@example.com")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }
}