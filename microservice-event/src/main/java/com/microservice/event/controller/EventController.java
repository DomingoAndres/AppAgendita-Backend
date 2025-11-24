package com.microservice.event.controller;

import com.microservice.event.dto.CreateEventDTO;
import com.microservice.event.dto.EventDTO;
import com.microservice.event.dto.ShareNoteDTO;
import com.microservice.event.dto.UpdateEventDTO;
import com.microservice.event.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDTO> createEvent(@Valid @RequestBody CreateEventDTO createEventDTO) {
        EventDTO createdEvent = eventService.createEvent(createEventDTO);
        return new ResponseEntity<>(createdEvent, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDTO> getEvent(@PathVariable UUID id) {
        Optional<EventDTO> eventDTOOptional = eventService.getEvent(id);
        return eventDTOOptional
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDTO> updateEvent(@PathVariable UUID id,
                                                @Valid @RequestBody UpdateEventDTO updateEventDTO) {
        updateEventDTO.setId(id);
        Optional<EventDTO> updatedEventOpt = eventService.updateEvent(updateEventDTO);
        return updatedEventOpt
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable UUID id) {
        eventService.deleteEvent(id);
    }

    @PostMapping("/invite")
    public ResponseEntity<Void> inviteParticipant(@Valid @RequestBody ShareNoteDTO shareNoteDTO) {
        boolean success = eventService.inviteParticipant(shareNoteDTO);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accept")
    public ResponseEntity<Void> acceptInvitation(@Valid @RequestBody ShareNoteDTO shareNoteDTO) {
        boolean success = eventService.acceptInvitation(shareNoteDTO);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/revoke")
    public ResponseEntity<Void> revokeAccess(@Valid @RequestBody ShareNoteDTO shareNoteDTO) {
        boolean success = eventService.revokeAccess(shareNoteDTO);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
