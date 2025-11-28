package com.microservice.event.service;

import com.microservice.event.assembler.EventAssembler;
import com.microservice.event.dto.CreateEventDTO;
import com.microservice.event.dto.EventDTO;
import com.microservice.event.dto.UpdateEventDTO;
import com.microservice.event.exception.EventNotFoundException;
import com.microservice.event.exception.InvalidEventDataException;
import com.microservice.event.exception.UnauthorizedEventAccessException;
import com.microservice.event.model.Event;
import com.microservice.event.repository.EventRepository;
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
public class EventService {

    private final EventRepository eventRepository;
    private final EventAssembler eventAssembler;

    @Autowired
    public EventService(EventRepository eventRepository, EventAssembler eventAssembler) {
        this.eventRepository = eventRepository;
        this.eventAssembler = eventAssembler;
    }

    // ============= CRUD BÁSICO =============

    @Transactional(readOnly = true)
    public List<EventDTO> getAllEventsByOwner(UUID ownerId) {
        log.debug("Obteniendo todos los eventos del owner: {}", ownerId);
        List<Event> events = eventRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId);
        return events.stream()
                .map(eventAssembler::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<EventDTO> getAllEventsByOwner(UUID ownerId, Pageable pageable) {
        log.debug("Obteniendo eventos paginados del owner: {}", ownerId);
        Page<Event> events = eventRepository.findByOwnerIdOrderByCreatedAtDesc(ownerId, pageable);
        return events.map(eventAssembler::toDTO);
    }

    @Transactional(readOnly = true)
    public EventDTO getEventByIdAndOwner(UUID eventId, UUID ownerId) {
        log.debug("Obteniendo evento {} del owner {}", eventId, ownerId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado con ID: " + eventId));

        if (!event.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedEventAccessException("No tienes permiso para acceder a este evento");
        }

        return eventAssembler.toDTO(event);
    }

    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        log.debug("Creando nuevo evento para owner: {}", createEventDTO.getOwnerId());

        validateCreateEventDTO(createEventDTO);

        Event event = eventAssembler.toEntity(createEventDTO);

        Event savedEvent = eventRepository.save(event);
        log.info("Evento creado exitosamente con ID: {}", savedEvent.getId());

        return eventAssembler.toDTO(savedEvent);
    }

    public EventDTO updateEvent(UUID eventId, UpdateEventDTO updateEventDTO, UUID ownerId) {
        log.debug("Actualizando evento {} del owner {}", eventId, ownerId);

        Event existingEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado con ID: " + eventId));

        if (!existingEvent.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedEventAccessException("No tienes permiso para actualizar este evento");
        }

        Event updatedEvent = eventAssembler.update(existingEvent, updateEventDTO);

        Event savedEvent = eventRepository.save(updatedEvent);
        log.info("Evento actualizado exitosamente: {}", eventId);

        return eventAssembler.toDTO(savedEvent);
    }

    public void deleteEvent(UUID eventId, UUID ownerId) {
        log.debug("Eliminando evento {} del owner {}", eventId, ownerId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Evento no encontrado con ID: " + eventId));

        if (!event.getOwnerId().equals(ownerId)) {
            throw new UnauthorizedEventAccessException("No tienes permiso para eliminar este evento");
        }

        eventRepository.delete(event);
        log.info("Evento eliminado exitosamente: {}", eventId);
    }

    // ============= OPERACIONES ESPECÍFICAS =============

    @Transactional(readOnly = true)
    public List<EventDTO> searchEvents(UUID ownerId, String searchText) {
        log.debug("Buscando eventos del owner {} con texto: {}", ownerId, searchText);
        List<Event> events = eventRepository.searchEvents(ownerId, searchText);
        return events.stream()
                .map(eventAssembler::toDTO)
                .toList();
    }

    public void deleteAllOwnerEvents(UUID ownerId) {
        log.debug("Eliminando todos los eventos del owner {}", ownerId);
        eventRepository.deleteByOwnerId(ownerId);
        log.info("Todos los eventos del owner {} han sido eliminados", ownerId);
    }

    @Transactional(readOnly = true)
    public Long getEventsCount(UUID ownerId) {
        return eventRepository.countByOwnerId(ownerId);
    }

    // ============= MÉTODOS PRIVADOS =============

    private void validateCreateEventDTO(CreateEventDTO createEventDTO) {
        if (createEventDTO.getOwnerId() == null) {
            throw new InvalidEventDataException("El ID del owner es requerido");
        }
        if (createEventDTO.getTitle() == null || createEventDTO.getTitle().trim().isEmpty()) {
            throw new InvalidEventDataException("El título del evento es requerido");
        }
        if (createEventDTO.getEventTimestamp() == null) {
            throw new InvalidEventDataException("La fecha y hora del evento es requerida");
        }
    }
}
