package com.microservice.event.dto;

import com.microservice.event.model.Event.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    private UUID id;
    private UUID noteId;
    private UUID ownerId;
    private Set<Participant> participants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
