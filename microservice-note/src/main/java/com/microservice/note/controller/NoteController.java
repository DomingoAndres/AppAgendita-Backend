package com.microservice.note.controller;

import com.microservice.note.dto.CreateNoteDTO;
import com.microservice.note.dto.NoteDTO;
import com.microservice.note.dto.UpdateNoteDTO;
import com.microservice.note.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "API para gestión de notas en AppAgendita")
@Slf4j
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    // ============= CRUD BÁSICO =============

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las notas de un usuario")
    public ResponseEntity<List<NoteDTO>> getAllNotesByUser(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/notes/user/{}", userId);
        List<NoteDTO> notes = noteService.getAllNotesByUser(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/user/{userId}/pageable")
    @Operation(summary = "Obtener notas de un usuario con paginación")
    public ResponseEntity<Page<NoteDTO>> getAllNotesByUserPageable(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            Pageable pageable) {
        log.debug("GET /api/notes/user/{}/pageable", userId);
        Page<NoteDTO> notes = noteService.getAllNotesByUser(userId, pageable);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/{noteId}/user/{userId}")
    @Operation(summary = "Obtener nota por ID y Usuario")
    public ResponseEntity<NoteDTO> getNoteByIdAndUser(
            @Parameter(description = "ID de la nota") @PathVariable UUID noteId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/notes/{}/user/{}", noteId, userId);
        NoteDTO note = noteService.getNoteByIdAndUser(noteId, userId);
        return ResponseEntity.ok(note);
    }

    @PostMapping
    @Operation(summary = "Crear nueva nota")
    public ResponseEntity<NoteDTO> createNote(
            @Parameter(description = "Datos para crear la nota") @Valid @RequestBody CreateNoteDTO createNoteDTO) {
        log.debug("POST /api/notes - Usuario: {}", createNoteDTO.getUserId());
        NoteDTO createdNote = noteService.createNote(createNoteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNote);
    }

    @PutMapping("/{noteId}/user/{userId}")
    @Operation(summary = "Actualizar nota")
    public ResponseEntity<NoteDTO> updateNote(
            @Parameter(description = "ID de la nota") @PathVariable UUID noteId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Datos para actualizar la nota") @Valid @RequestBody UpdateNoteDTO updateNoteDTO) {
        log.debug("PUT /api/notes/{}/user/{}", noteId, userId);
        NoteDTO updatedNote = noteService.updateNote(noteId, updateNoteDTO, userId);
        return ResponseEntity.ok(updatedNote);
    }

    @DeleteMapping("/{noteId}/user/{userId}")
    @Operation(summary = "Eliminar nota")
    public ResponseEntity<Map<String, String>> deleteNote(
            @Parameter(description = "ID de la nota") @PathVariable UUID noteId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("DELETE /api/notes/{}/user/{}", noteId, userId);
        noteService.deleteNote(noteId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Nota eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    // ============= OPERACIONES ESPECÍFICAS =============

    @GetMapping("/user/{userId}/search")
    @Operation(summary = "Buscar notas")
    public ResponseEntity<List<NoteDTO>> searchNotes(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Texto a buscar") @RequestParam String searchText) {
        log.debug("GET /api/notes/user/{}/search?searchText={}", userId, searchText);
        List<NoteDTO> notes = noteService.searchNotes(userId, searchText);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Obtener cantidad total de notas del usuario")
    public ResponseEntity<Map<String, Long>> getNotesCount(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/notes/user/{}/count", userId);
        Long count = noteService.getNotesCount(userId);

        Map<String, Long> response = new HashMap<>();
        response.put("totalNotes", count);
        return ResponseEntity.ok(response);
    }

    // ============= OPERACIONES MASIVAS =============

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Eliminar todas las notas de un usuario")
    public ResponseEntity<Map<String, String>> deleteAllUserNotes(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("DELETE /api/notes/user/{}", userId);
        noteService.deleteAllUserNotes(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Todas las notas del usuario han sido eliminadas");
        return ResponseEntity.ok(response);
    }
}