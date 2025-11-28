package com.microservice.event.controller;

import com.microservice.event.dto.CreateEventDTO;
import com.microservice.event.dto.EventDTO;
import com.microservice.event.dto.UpdateEventDTO;
import com.microservice.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    private UUID parseUserId(String userIdHeader) {
        try {
            return UUID.fromString(userIdHeader);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id inválido");
        }
    }

    // POST /api/events
    @PostMapping
    public ResponseEntity<EventDTO> createEvent(
            @RequestHeader("X-User-Id") String userIdHeader,
            @Valid @RequestBody CreateEventDTO createEventDTO) {

        UUID ownerId = parseUserId(userIdHeader);
        createEventDTO.setOwnerId(ownerId); // NO confiar en el body del cliente

        EventDTO created = eventService.createEvent(createEventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/events
    @GetMapping
    public ResponseEntity<List<EventDTO>> getEvents(
            @RequestHeader("X-User-Id") String userIdHeader) {

        UUID ownerId = parseUserId(userIdHeader);
        List<EventDTO> events = eventService.getAllEventsByOwner(ownerId);
        return ResponseEntity.ok(events);
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable("id") UUID eventId) {

        UUID ownerId = parseUserId(userIdHeader);
        EventDTO event = eventService.getEventByIdAndOwner(eventId, ownerId);
        return ResponseEntity.ok(event);
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable("id") UUID eventId,
            @Valid @RequestBody UpdateEventDTO updateEventDTO) {

        UUID ownerId = parseUserId(userIdHeader);
        updateEventDTO.setId(eventId); // aseguramos consistencia con la URL

        EventDTO updated = eventService.updateEvent(eventId, updateEventDTO, ownerId);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @RequestHeader("X-User-Id") String userIdHeader,
            @PathVariable("id") UUID eventId) {

        UUID ownerId = parseUserId(userIdHeader);
        eventService.deleteEvent(eventId, ownerId);
        return ResponseEntity.noContent().build();
    }
}
