package com.microservice.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShareNoteDTO {

    @NotNull(message = "Event ID is required")
    private UUID eventId;

    @NotNull(message = "User ID is required")
    private UUID userId;

    @NotNull(message = "Permission is required")
    private String permission; // Expected values: READ, WRITE
}
