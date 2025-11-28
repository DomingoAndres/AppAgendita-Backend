package com.microservice.note.service;

import com.microservice.note.assembler.NoteAssembler;
import com.microservice.note.dto.CreateNoteDTO;
import com.microservice.note.dto.NoteDTO;
import com.microservice.note.dto.UpdateNoteDTO;
import com.microservice.note.exception.InvalidNoteDataException;
import com.microservice.note.exception.NoteNotFoundException;
import com.microservice.note.exception.UnauthorizedNoteAccessException;
import com.microservice.note.model.Note;
import com.microservice.note.repository.NoteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private NoteAssembler noteAssembler;

    @InjectMocks
    private NoteService noteService;

    // --- Tests para CREATE ---

    @Test
    void createNote_ShouldSaveAndReturnDTO_WhenDataIsValid() {
        // Arrange (Preparar)
        UUID userId = UUID.randomUUID();
        CreateNoteDTO createDTO = new CreateNoteDTO("Title", "Desc", null, userId);
        Note noteEntity = new Note();
        noteEntity.setId(UUID.randomUUID());
        
        // Simulamos el comportamiento de las dependencias
        when(noteAssembler.fromCreateDTO(createDTO)).thenReturn(noteEntity);
        when(noteRepository.save(noteEntity)).thenReturn(noteEntity);
        when(noteAssembler.toDTO(noteEntity)).thenReturn(new NoteDTO());

        // Act (Ejecutar)
        NoteDTO result = noteService.createNote(createDTO);

        // Assert (Verificar)
        assertNotNull(result);
        verify(noteRepository).save(noteEntity); // Verificamos que se llamó a guardar
    }

    @Test
    void createNote_ShouldThrowException_WhenTitleIsMissing() {
        CreateNoteDTO createDTO = new CreateNoteDTO(null, "Desc", null, UUID.randomUUID());

        assertThrows(InvalidNoteDataException.class, () -> {
            noteService.createNote(createDTO);
        });
        
        // Verificamos que NUNCA se llamó al repositorio
        verify(noteRepository, never()).save(any());
    }

    // --- Tests para GET ---

    @Test
    void getNoteByIdAndUser_ShouldReturnNote_WhenFoundAndUserMatches() {
        // Arrange
        UUID noteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Note note = Note.builder().id(noteId).userId(userId).build();
        NoteDTO expectedDTO = new NoteDTO();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));
        when(noteAssembler.toDTO(note)).thenReturn(expectedDTO);

        // Act
        NoteDTO result = noteService.getNoteByIdAndUser(noteId, userId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDTO, result);
    }

    @Test
    void getNoteByIdAndUser_ShouldThrowNotFound_WhenNoteDoesNotExist() {
        UUID noteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        when(noteRepository.findById(noteId)).thenReturn(Optional.empty());

        assertThrows(NoteNotFoundException.class, () -> {
            noteService.getNoteByIdAndUser(noteId, userId);
        });
    }

    @Test
    void getNoteByIdAndUser_ShouldThrowUnauthorized_WhenUserDoesNotMatch() {
        // Arrange
        UUID noteId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID anotherUserId = UUID.randomUUID(); // Usuario diferente
        Note note = Note.builder().id(noteId).userId(ownerId).build();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        // Act & Assert
        assertThrows(UnauthorizedNoteAccessException.class, () -> {
            noteService.getNoteByIdAndUser(noteId, anotherUserId);
        });
    }

    @Test
    void getAllNotesByUser_ShouldReturnList() {
        UUID userId = UUID.randomUUID();
        List<Note> notes = Collections.singletonList(new Note());
        List<NoteDTO> dtos = Collections.singletonList(new NoteDTO());

        when(noteRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(notes);
        when(noteAssembler.toDTOList(notes)).thenReturn(dtos);

        List<NoteDTO> result = noteService.getAllNotesByUser(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // --- Tests para UPDATE ---

    @Test
    void updateNote_ShouldUpdate_WhenUserMatches() {
        UUID noteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UpdateNoteDTO updateDTO = new UpdateNoteDTO("New Title", "New Desc", null);
        
        Note existingNote = Note.builder().id(noteId).userId(userId).title("Old").build();
        Note updatedNote = Note.builder().id(noteId).userId(userId).title("New Title").build();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(existingNote));
        when(noteAssembler.updateFromDTO(existingNote, updateDTO)).thenReturn(updatedNote);
        when(noteRepository.save(updatedNote)).thenReturn(updatedNote);
        when(noteAssembler.toDTO(updatedNote)).thenReturn(new NoteDTO());

        NoteDTO result = noteService.updateNote(noteId, updateDTO, userId);

        assertNotNull(result);
        verify(noteRepository).save(updatedNote);
    }

    // --- Tests para DELETE ---

    @Test
    void deleteNote_ShouldDelete_WhenUserMatches() {
        UUID noteId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        Note note = Note.builder().id(noteId).userId(userId).build();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        noteService.deleteNote(noteId, userId);

        verify(noteRepository).delete(note);
    }

    @Test
    void deleteNote_ShouldThrowUnauthorized_WhenUserDoesNotMatch() {
        UUID noteId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID intruderId = UUID.randomUUID();
        Note note = Note.builder().id(noteId).userId(ownerId).build();

        when(noteRepository.findById(noteId)).thenReturn(Optional.of(note));

        assertThrows(UnauthorizedNoteAccessException.class, () -> {
            noteService.deleteNote(noteId, intruderId);
        });
        
        verify(noteRepository, never()).delete(any());
    }
}