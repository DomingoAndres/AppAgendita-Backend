package com.microservice.microservice_task.dto;

import com.microservice.microservice_task.model.TaskCategory;
import com.microservice.microservice_task.model.TaskPriority;
import com.microservice.microservice_task.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskFilterDTO {
    private UUID userId;
    private List<TaskStatus> statuses;
    private List<TaskPriority> priorities;
    private List<TaskCategory> categories;
    private LocalDate dueDateFrom;
    private LocalDate dueDateTo;
    private String searchText;
    private List<String> tags;
    private Boolean isOverdue;
    private Boolean isRecurring;
    private UUID parentTaskId;
}