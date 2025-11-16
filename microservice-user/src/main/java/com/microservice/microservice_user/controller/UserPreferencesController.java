package com.microservice.microservice_user.controller;

import com.microservice.microservice_user.dto.UserPreferencesDTO;
import com.microservice.microservice_user.service.UserPreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/preferences")
@Tag(name = "User Preferences", description = "API para preferencias de usuario")
public class UserPreferencesController {

    private final UserPreferencesService preferencesService;

    @Autowired
    public UserPreferencesController(UserPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @GetMapping
    @Operation(summary = "Obtener preferencias del usuario")
    public ResponseEntity<UserPreferencesDTO> getUserPreferences(@PathVariable UUID userId) {
        UserPreferencesDTO preferences = preferencesService.getUserPreferences(userId);
        return ResponseEntity.ok(preferences);
    }

    @PutMapping
    @Operation(summary = "Actualizar preferencias del usuario")
    public ResponseEntity<UserPreferencesDTO> updateUserPreferences(
            @PathVariable UUID userId,
            @RequestBody UserPreferencesDTO preferencesDTO) {
        UserPreferencesDTO updatedPreferences = preferencesService.updateUserPreferences(userId, preferencesDTO);
        return ResponseEntity.ok(updatedPreferences);
    }
}