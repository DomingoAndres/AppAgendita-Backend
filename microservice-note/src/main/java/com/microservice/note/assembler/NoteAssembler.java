package com.microservice.note.assembler;

import com.microservice.note.dto.CreateNoteDTO;
import com.microservice.note.dto.NoteDTO;
import com.microservice.note.dto.UpdateNoteDTO;
import com.microservice.note.model.Note;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NoteAssembler {

    /**
     * Convierte una Entidad Note a un NoteDTO para enviar al cliente.
     */
    public NoteDTO toDTO(Note note) {
        if (note == null) {
            return null;
        }

        return new NoteDTO(
                note.getId(),
                note.getTitle(),
                note.getDescription(),
                note.getImageUri(),
                note.getUserId(),
                note.getCreatedAt(),
                note.getUpdatedAt()
        );
    }

    /**
     * Convierte una lista de Entidades a una lista de DTOs.
     */
    public List<NoteDTO> toDTOList(List<Note> notes) {
        if (notes == null) {
            return List.of();
        }
        return notes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva Entidad Note a partir de un CreateNoteDTO.
     */
    public Note fromCreateDTO(CreateNoteDTO createNoteDTO) {
        if (createNoteDTO == null) {
            return null;
        }

        return Note.builder()
                .title(createNoteDTO.getTitle())
                .description(createNoteDTO.getDescription())
                .imageUri(createNoteDTO.getImageUri())
                .userId(createNoteDTO.getUserId())
                .build();
    }

    /**
     * Actualiza una Entidad Note existente con los datos de un UpdateNoteDTO.
     * Solo actualiza los campos que vienen en el DTO (título, descripción, imagen).
     * NO toca el ID, userId ni las fechas.
     */
    public Note updateFromDTO(Note noteToUpdate, UpdateNoteDTO updateNoteDTO) {
        if (updateNoteDTO == null) {
            return noteToUpdate;
        }

        // Solo actualizamos si el valor no es nulo (opcional)
        // O sobrescribimos todo si esa es la regla de negocio. 
        // Aquí sobrescribimos:
        
        if (updateNoteDTO.getTitle() != null) {
            noteToUpdate.setTitle(updateNoteDTO.getTitle());
        }
        
        if (updateNoteDTO.getDescription() != null) {
            noteToUpdate.setDescription(updateNoteDTO.getDescription());
        }
        
        if (updateNoteDTO.getImageUri() != null) {
            noteToUpdate.setImageUri(updateNoteDTO.getImageUri());
        }

        return noteToUpdate;
    }
}