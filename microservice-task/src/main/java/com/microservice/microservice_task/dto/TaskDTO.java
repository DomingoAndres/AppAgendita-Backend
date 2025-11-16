package com.microservice.microservice_task.dto;

import com.microservice.microservice_task.model.TaskCategory;
import com.microservice.microservice_task.model.TaskPriority;
import com.microservice.microservice_task.model.TaskStatus;
import com.microservice.microservice_task.model.RecurrenceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDTO {
    private UUID id;
    private String title;
    private String description;
    private UUID userId;
    private TaskStatus status;
    private TaskPriority priority;
    private TaskCategory category;
    private LocalDateTime dueDate;
    private LocalDateTime reminderDate;
    private LocalDateTime completedDate;
    private Integer estimatedDurationMinutes;
    private Integer actualDurationMinutes;
    private List<String> tags;
    private List<String> attachmentUrls;
    private String location;
    private Boolean isRecurring;
    private RecurrenceType recurrenceType;
    private LocalDateTime recurrenceEndDate;
    private UUID parentTaskId;
    private Integer orderIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Campos calculados
    private Boolean isOverdue;
    private Long daysUntilDue;
    private String statusDisplayName;
    private String priorityDisplayName;
    private String categoryDisplayName;
}