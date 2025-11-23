package com.microservice.note.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateNoteDTO {

    @NotBlank(message = "El título de la nota es requerido")
    @Size(max = 200, message = "El título no puede exceder los 200 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción es demasiado larga")
    private String description;

    private String imageUri;

    @NotNull(message = "El ID del usuario es requerido")
    private UUID userId;
}