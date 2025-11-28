package com.microservice.note.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.note.dto.CreateNoteDTO;
import com.microservice.note.dto.NoteDTO;
import com.microservice.note.dto.UpdateNoteDTO;
import com.microservice.note.exception.NoteNotFoundException;
import com.microservice.note.service.NoteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration; // Importar esto
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// CAMBIO CLAVE: excludeAutoConfiguration = SecurityAutoConfiguration.class
// Esto desactiva Spring Security SOLO para este test.
@WebMvcTest(value = NoteController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean 
    private NoteService noteService;

    @Autowired
    private ObjectMapper objectMapper;

    private NoteDTO noteDTO;
    private UUID userId;
    private UUID noteId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        noteId = UUID.randomUUID();

        noteDTO = new NoteDTO();
        noteDTO.setId(noteId);
        noteDTO.setTitle("Test Note");
        noteDTO.setDescription("Description");
        noteDTO.setUserId(userId);
    }

    // ... (El resto de los métodos @Test se mantienen EXACTAMENTE IGUAL) ...
    // Copia y pega el resto de métodos que tenías: getAllNotesByUser, getNoteByIdAndUser, createNote, etc.
    
    // --- GET /api/notes/user/{userId} ---

    @Test
    void getAllNotesByUser_ShouldReturnList_WhenNotesExist() throws Exception {
        List<NoteDTO> notes = Arrays.asList(noteDTO, new NoteDTO());
        
        when(noteService.getAllNotesByUser(userId)).thenReturn(notes);

        mockMvc.perform(get("/api/notes/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].title").value("Test Note"));
    }

    // --- GET /api/notes/{noteId}/user/{userId} ---

    @Test
    void getNoteByIdAndUser_ShouldReturnNote_WhenFound() throws Exception {
        when(noteService.getNoteByIdAndUser(noteId, userId)).thenReturn(noteDTO);

        mockMvc.perform(get("/api/notes/{noteId}/user/{userId}", noteId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(noteId.toString()))
                .andExpect(jsonPath("$.title").value(noteDTO.getTitle()));
    }

    @Test
    void getNoteByIdAndUser_ShouldReturn404_WhenNotFound() throws Exception {
        when(noteService.getNoteByIdAndUser(noteId, userId))
                .thenThrow(new NoteNotFoundException("Nota no encontrada"));

        mockMvc.perform(get("/api/notes/{noteId}/user/{userId}", noteId, userId))
                .andExpect(status().isNotFound());
    }

    // --- POST /api/notes ---

    @Test
    void createNote_ShouldReturnCreated_WhenDataIsValid() throws Exception {
        CreateNoteDTO createDTO = new CreateNoteDTO("Title", "Desc", null, userId);
        
        when(noteService.createNote(any(CreateNoteDTO.class))).thenReturn(noteDTO);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(noteDTO.getTitle()));
    }

    @Test
    void createNote_ShouldReturn400_WhenTitleIsMissing() throws Exception {
        CreateNoteDTO invalidDTO = new CreateNoteDTO(null, "Desc", null, userId);

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    // --- PUT /api/notes/{noteId}/user/{userId} ---

    @Test
    void updateNote_ShouldReturnUpdatedNote_WhenSuccessful() throws Exception {
        UpdateNoteDTO updateDTO = new UpdateNoteDTO("Updated Title", "Desc", null);
        NoteDTO updatedNoteDTO = new NoteDTO();
        updatedNoteDTO.setTitle("Updated Title");

        when(noteService.updateNote(eq(noteId), any(UpdateNoteDTO.class), eq(userId)))
                .thenReturn(updatedNoteDTO);

        mockMvc.perform(put("/api/notes/{noteId}/user/{userId}", noteId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    // --- DELETE /api/notes/{noteId}/user/{userId} ---

    @Test
    void deleteNote_ShouldReturnOk_WhenSuccessful() throws Exception {
        mockMvc.perform(delete("/api/notes/{noteId}/user/{userId}", noteId, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Nota eliminada exitosamente"));
    }
}