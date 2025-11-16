package com.microservice.microservice_task.dto;

import com.microservice.microservice_task.model.TaskCategory;
import com.microservice.microservice_task.model.TaskPriority;
import com.microservice.microservice_task.model.RecurrenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskDTO {
    
    @NotBlank(message = "El título de la tarea es requerido")
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    private String title;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    @NotNull(message = "El ID del usuario es requerido")
    private UUID userId;

    private TaskPriority priority = TaskPriority.MEDIUM;
    private TaskCategory category;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;
    private Integer estimatedDurationMinutes;
    private List<String> tags;
    private String location;
    private Boolean isRecurring = false;
    private RecurrenceType recurrenceType;
    private LocalDateTime recurrenceEndDate;
    private UUID parentTaskId;
    private Integer orderIndex = 0;
}