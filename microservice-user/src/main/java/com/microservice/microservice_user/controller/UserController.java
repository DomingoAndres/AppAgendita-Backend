package com.microservice.microservice_user.controller;

import com.microservice.microservice_user.dto.*;
import com.microservice.microservice_user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "API para gestión de usuarios")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los usuarios")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/pageable")
    @Operation(summary = "Obtener usuarios con paginación")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener usuario por ID")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Obtener usuario por nombre de usuario")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Obtener usuario por email")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        UserDTO user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios")
    public ResponseEntity<Page<UserDTO>> searchUsers(
            @RequestParam String search,
            Pageable pageable) {
        Page<UserDTO> users = userService.searchUsers(search, pageable);
        return ResponseEntity.ok(users);
    }

    @PostMapping
    @Operation(summary = "Crear nuevo usuario")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO) {
        UserDTO createdUser = userService.createUser(createUserDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar usuario")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUser = userService.updateUser(id, updateUserDTO);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/change-password")
    @Operation(summary = "Cambiar contraseña")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordDTO changePasswordDTO) {
        userService.changePassword(id, changePasswordDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña cambiada exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "Desactivar usuario")
    public ResponseEntity<Map<String, String>> deactivateUser(@PathVariable UUID id) {
        userService.deactivateUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario desactivado exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "Activar usuario")
    public ResponseEntity<Map<String, String>> activateUser(@PathVariable UUID id) {
        userService.activateUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Usuario activado exitosamente");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/verify-email")
    @Operation(summary = "Verificar email")
    public ResponseEntity<Map<String, String>> verifyEmail(@PathVariable UUID id) {
        userService.verifyEmail(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Email verificado exitosamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "Obtener estadísticas de usuarios")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/created-between")
    @Operation(summary = "Obtener usuarios creados entre fechas")
    public ResponseEntity<List<UserDTO>> getUsersCreatedBetween(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        List<UserDTO> users = userService.getUsersCreatedBetween(startDate, endDate);
        return ResponseEntity.ok(users);
    }
}