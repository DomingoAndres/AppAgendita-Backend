package com.microservice.note.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateNoteDTO {

    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    private String title;

    @Size(max = 2000, message = "La descripción es demasiado larga")
    private String description;

    private String imageUri;
}