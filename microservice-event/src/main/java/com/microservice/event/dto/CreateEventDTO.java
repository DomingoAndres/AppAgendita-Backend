package com.microservice.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEventDTO {

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String description;

    @NotNull(message = "La fecha y hora del evento es obligatoria")
    private LocalDateTime eventTimestamp;

    private String location;

    // NO vendrá del cliente, se rellenará desde el header X-User-Id en el controller
    private UUID ownerId;
}
