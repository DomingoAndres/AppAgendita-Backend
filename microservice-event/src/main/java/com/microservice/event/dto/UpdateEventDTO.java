package com.microservice.event.dto;

import com.microservice.event.model.Event.Participant;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateEventDTO {

    @NotNull(message = "Event ID is required")
    private UUID id;

    private Set<Participant> participants;
}
