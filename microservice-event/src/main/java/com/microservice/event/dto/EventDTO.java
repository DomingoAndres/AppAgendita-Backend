package com.microservice.event.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {

    private UUID id;
    private UUID ownerId;
    private String title;
    private String description;
    private LocalDateTime eventTimestamp;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
