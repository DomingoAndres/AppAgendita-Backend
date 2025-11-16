package com.microservice.microservice_task.dto;

import com.microservice.microservice_task.model.TaskCategory;
import com.microservice.microservice_task.model.TaskPriority;
import com.microservice.microservice_task.model.TaskStatus;
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
public class UpdateTaskDTO {
    
    @Size(min = 1, max = 200, message = "El título debe tener entre 1 y 200 caracteres")
    private String title;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String description;

    private TaskStatus status;
    private TaskPriority priority;
    private TaskCategory category;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;
    private Integer estimatedDurationMinutes;
    private Integer actualDurationMinutes;
    private List<String> tags;
    private List<String> attachmentUrls;
    private String location;
    private UUID parentTaskId;
    private Integer orderIndex;
}