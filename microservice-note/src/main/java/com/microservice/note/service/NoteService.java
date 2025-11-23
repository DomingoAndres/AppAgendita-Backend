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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteAssembler noteAssembler;

    @Autowired
    public NoteService(NoteRepository noteRepository, NoteAssembler noteAssembler) {
        this.noteRepository = noteRepository;
        this.noteAssembler = noteAssembler;
    }

    // ============= CRUD BÁSICO =============

    @Transactional(readOnly = true)
    public List<NoteDTO> getAllNotesByUser(UUID userId) {
        log.debug("Obteniendo todas las notas del usuario: {}", userId);
        List<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return noteAssembler.toDTOList(notes);
    }

    @Transactional(readOnly = true)
    public Page<NoteDTO> getAllNotesByUser(UUID userId, Pageable pageable) {
        log.debug("Obteniendo notas paginadas del usuario: {}", userId);
        Page<Note> notes = noteRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return notes.map(noteAssembler::toDTO);
    }

    @Transactional(readOnly = true)
    public NoteDTO getNoteByIdAndUser(UUID noteId, UUID userId) {
        log.debug("Obteniendo nota {} del usuario {}", noteId, userId);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Nota no encontrada con ID: " + noteId));

        // Validación de seguridad: ¿La nota pertenece al usuario que la pide?
        if (!note.getUserId().equals(userId)) {
            throw new UnauthorizedNoteAccessException("No tienes permiso para acceder a esta nota");
        }

        return noteAssembler.toDTO(note);
    }

    public NoteDTO createNote(CreateNoteDTO createNoteDTO) {
        log.debug("Creando nueva nota para usuario: {}", createNoteDTO.getUserId());

        // Validar datos básicos
        validateCreateNoteDTO(createNoteDTO);

        // Convertir DTO a Entidad
        Note note = noteAssembler.fromCreateDTO(createNoteDTO);

        // Guardar
        Note savedNote = noteRepository.save(note);
        log.info("Nota creada exitosamente con ID: {}", savedNote.getId());

        return noteAssembler.toDTO(savedNote);
    }

    public NoteDTO updateNote(UUID noteId, UpdateNoteDTO updateNoteDTO, UUID userId) {
        log.debug("Actualizando nota {} del usuario {}", noteId, userId);

        Note existingNote = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Nota no encontrada con ID: " + noteId));

        // Seguridad
        if (!existingNote.getUserId().equals(userId)) {
            throw new UnauthorizedNoteAccessException("No tienes permiso para actualizar esta nota");
        }

        // Actualizar campos usando el Assembler
        Note updatedNote = noteAssembler.updateFromDTO(existingNote, updateNoteDTO);
        
        Note savedNote = noteRepository.save(updatedNote);
        log.info("Nota actualizada exitosamente: {}", noteId);

        return noteAssembler.toDTO(savedNote);
    }

    public void deleteNote(UUID noteId, UUID userId) {
        log.debug("Eliminando nota {} del usuario {}", noteId, userId);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new NoteNotFoundException("Nota no encontrada con ID: " + noteId));

        // Seguridad
        if (!note.getUserId().equals(userId)) {
            throw new UnauthorizedNoteAccessException("No tienes permiso para eliminar esta nota");
        }

        noteRepository.delete(note);
        log.info("Nota eliminada exitosamente: {}", noteId);
    }

    // ============= OPERACIONES ESPECÍFICAS =============

    @Transactional(readOnly = true)
    public List<NoteDTO> searchNotes(UUID userId, String searchText) {
        log.debug("Buscando notas del usuario {} con texto: {}", userId, searchText);
        List<Note> notes = noteRepository.searchNotes(userId, searchText);
        return noteAssembler.toDTOList(notes);
    }

    public void deleteAllUserNotes(UUID userId) {
        log.debug("Eliminando todas las notas del usuario {}", userId);
        noteRepository.deleteByUserId(userId);
        log.info("Todas las notas del usuario {} han sido eliminadas", userId);
    }

    @Transactional(readOnly = true)
    public Long getNotesCount(UUID userId) {
        return noteRepository.countByUserId(userId);
    }

    // ============= MÉTODOS PRIVADOS =============

    private void validateCreateNoteDTO(CreateNoteDTO createNoteDTO) {
        if (createNoteDTO.getTitle() == null || createNoteDTO.getTitle().trim().isEmpty()) {
            throw new InvalidNoteDataException("El título de la nota es requerido");
        }

        if (createNoteDTO.getUserId() == null) {
            throw new InvalidNoteDataException("El ID del usuario es requerido");
        }
    }
}