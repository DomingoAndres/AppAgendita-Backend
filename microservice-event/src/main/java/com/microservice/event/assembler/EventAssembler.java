package com.microservice.event.assembler;

import com.microservice.event.dto.CreateEventDTO;
import com.microservice.event.dto.EventDTO;
import com.microservice.event.dto.UpdateEventDTO;
import com.microservice.event.model.Event;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@NoArgsConstructor
public class EventAssembler {

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        return EventDTO.builder()
                .id(event.getId())
                .noteId(event.getNoteId())
                .ownerId(event.getOwnerId())
                .participants(event.getParticipants())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();
    }

    public Event toEntity(CreateEventDTO createEventDTO) {
        if (createEventDTO == null) {
            return null;
        }
        return Event.builder()
                .id(null)
                .noteId(createEventDTO.getNoteId())
                .ownerId(createEventDTO.getOwnerId())
                .participants(createEventDTO.getParticipants())
                .build();
    }

    public Event update(Event existingEvent, UpdateEventDTO updateEventDTO) {
        if (existingEvent == null || updateEventDTO == null) {
            return existingEvent;
        }
        if (updateEventDTO.getParticipants() != null) {
            existingEvent.setParticipants(updateEventDTO.getParticipants());
        }
        return existingEvent;
    }
}
