package com.microservice.event.service;

import com.microservice.event.dto.CreateEventDTO;
import com.microservice.event.dto.EventDTO;
import com.microservice.event.dto.ShareNoteDTO;
import com.microservice.event.dto.UpdateEventDTO;
import com.microservice.event.model.Event;
import com.microservice.event.model.Event.Participant.Permission;
import com.microservice.event.repository.EventRepository;
import com.microservice.event.assembler.EventAssembler;
import com.microservice.event.client.UserServiceClient;
import com.microservice.event.client.NoteServiceClient;
import com.microservice.event.client.NotificationServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventRepository eventRepository;
    private final EventAssembler eventAssembler;
    private final UserServiceClient userServiceClient;
    private final NoteServiceClient noteServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public EventDTO createEvent(CreateEventDTO createEventDTO) {
        // TODO: Validate user and note existence via Feign clients
        Event event = eventAssembler.toEntity(createEventDTO);
        event = eventRepository.save(event);
        return eventAssembler.toDTO(event);
    }

    public Optional<EventDTO> getEvent(UUID eventId) {
        return eventRepository.findById(eventId).map(eventAssembler::toDTO);
    }

    public Optional<EventDTO> updateEvent(UpdateEventDTO updateEventDTO) {
        return eventRepository.findById(updateEventDTO.getId())
                .map(existingEvent -> {
                    // TODO: Update fields as appropriate, e.g., participants management
                    Event updatedEvent = eventAssembler.update(existingEvent, updateEventDTO);
                    return eventAssembler.toDTO(eventRepository.save(updatedEvent));
                });
    }

    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(eventId);
    }

    public boolean inviteParticipant(ShareNoteDTO shareNoteDTO) {
        // TODO: Validate user via UserServiceClient and note via NoteServiceClient
        // Add participant to event with permission, save, notify
        return true;
    }

    public boolean acceptInvitation(ShareNoteDTO shareNoteDTO) {
        // TODO: Accept invitation logic
        return true;
    }

    public boolean revokeAccess(ShareNoteDTO shareNoteDTO) {
        // TODO: Revoke access logic
        return true;
    }
}
