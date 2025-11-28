package com.microservice.event.assembler;

import com.microservice.event.dto.CreateEventDTO;
import com.microservice.event.dto.EventDTO;
import com.microservice.event.dto.UpdateEventDTO;
import com.microservice.event.model.Event;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EventAssembler {

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        return EventDTO.builder()
                .id(event.getId())
                .ownerId(event.getOwnerId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventTimestamp(event.getEventTimestamp())
                .location(event.getLocation())
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
                .ownerId(createEventDTO.getOwnerId())
                .title(createEventDTO.getTitle())
                .description(createEventDTO.getDescription())
                .eventTimestamp(createEventDTO.getEventTimestamp())
                .location(createEventDTO.getLocation())
                .build();
    }

    public Event update(Event existingEvent, UpdateEventDTO updateEventDTO) {
        if (existingEvent == null || updateEventDTO == null) {
            return existingEvent;
        }
        if (updateEventDTO.getTitle() != null) {
            existingEvent.setTitle(updateEventDTO.getTitle());
        }
        if (updateEventDTO.getDescription() != null) {
            existingEvent.setDescription(updateEventDTO.getDescription());
        }
        if (updateEventDTO.getEventTimestamp() != null) {
            existingEvent.setEventTimestamp(updateEventDTO.getEventTimestamp());
        }
        if (updateEventDTO.getLocation() != null) {
            existingEvent.setLocation(updateEventDTO.getLocation());
        }
        return existingEvent;
    }
}
