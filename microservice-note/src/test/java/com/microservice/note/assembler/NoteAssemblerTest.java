package com.microservice.note.assembler;

import com.microservice.note.dto.CreateNoteDTO;
import com.microservice.note.dto.NoteDTO;
import com.microservice.note.dto.UpdateNoteDTO;
import com.microservice.note.model.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NoteAssemblerTest {

    private NoteAssembler noteAssembler;

    @BeforeEach
    void setUp() {
        // Instanciamos la clase a probar antes de cada test
        noteAssembler = new NoteAssembler();
    }

    @Test
    void toDTO_ShouldConvertNoteToNoteDTO() {
        // 1. Arrange (Preparar datos)
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        Note note = Note.builder()
                .id(id)
                .title("Test Title")
                .description("Test Description")
                .imageUri("http://image.url")
                .userId(userId)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // 2. Act (Ejecutar método)
        NoteDTO result = noteAssembler.toDTO(note);

        // 3. Assert (Verificar resultados)
        assertNotNull(result);
        assertEquals(note.getId(), result.getId());
        assertEquals(note.getTitle(), result.getTitle());
        assertEquals(note.getDescription(), result.getDescription());
        assertEquals(note.getImageUri(), result.getImageUri());
        assertEquals(note.getUserId(), result.getUserId());
        assertEquals(note.getCreatedAt(), result.getCreatedAt());
        assertEquals(note.getUpdatedAt(), result.getUpdatedAt());
    }

    @Test
    void toDTO_ShouldReturnNull_WhenInputIsNull() {
        NoteDTO result = noteAssembler.toDTO(null);
        assertNull(result);
    }

    @Test
    void toDTOList_ShouldConvertListOfNotes() {
        // Arrange
        Note note1 = Note.builder().id(UUID.randomUUID()).title("Note 1").build();
        Note note2 = Note.builder().id(UUID.randomUUID()).title("Note 2").build();
        List<Note> notes = Arrays.asList(note1, note2);

        // Act
        List<NoteDTO> result = noteAssembler.toDTOList(notes);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(note1.getId(), result.get(0).getId());
        assertEquals(note2.getId(), result.get(1).getId());
    }

    @Test
    void toDTOList_ShouldReturnEmptyList_WhenInputIsNull() {
        List<NoteDTO> result = noteAssembler.toDTOList(null);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void fromCreateDTO_ShouldConvertDTOToNote() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateNoteDTO createDTO = new CreateNoteDTO(
                "New Title",
                "New Description",
                "http://new.image",
                userId
        );

        // Act
        Note result = noteAssembler.fromCreateDTO(createDTO);

        // Assert
        assertNotNull(result);
        assertNull(result.getId()); // El ID se genera en la BD, debe ser null aquí
        assertEquals(createDTO.getTitle(), result.getTitle());
        assertEquals(createDTO.getDescription(), result.getDescription());
        assertEquals(createDTO.getImageUri(), result.getImageUri());
        assertEquals(createDTO.getUserId(), result.getUserId());
    }

    @Test
    void fromCreateDTO_ShouldReturnNull_WhenInputIsNull() {
        Note result = noteAssembler.fromCreateDTO(null);
        assertNull(result);
    }

    @Test
    void updateFromDTO_ShouldUpdateOnlyNotNullFields() {
        // Arrange
        Note existingNote = Note.builder()
                .id(UUID.randomUUID())
                .title("Old Title")
                .description("Old Description")
                .imageUri("http://old.image")
                .build();

        UpdateNoteDTO updateDTO = new UpdateNoteDTO(
                "Updated Title",
                null, // Description es null, no debería cambiar
                "http://updated.image"
        );

        // Act
        Note result = noteAssembler.updateFromDTO(existingNote, updateDTO);

        // Assert
        assertEquals("Updated Title", result.getTitle());      // Cambió
        assertEquals("Old Description", result.getDescription()); // Se mantuvo
        assertEquals("http://updated.image", result.getImageUri()); // Cambió
    }
}