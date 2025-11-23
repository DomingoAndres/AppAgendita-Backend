package com.microservice.note.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteDTO {
    private UUID id;
    private String title;
    private String description;
    private String imageUri;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}