package com.microservice.microservice_task.assembler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservice.microservice_task.dto.*;
import com.microservice.microservice_task.model.Task;
import com.microservice.microservice_task.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TaskAssembler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TaskDTO toDTO(Task task) {
        if (task == null) {
            return null;
        }

        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setUserId(task.getUserId());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCategory(task.getCategory());
        dto.setDueDate(task.getDueDate());
        dto.setReminderDate(task.getReminderDate());
        dto.setCompletedDate(task.getCompletedDate());
        dto.setEstimatedDurationMinutes(task.getEstimatedDurationMinutes());
        dto.setActualDurationMinutes(task.getActualDurationMinutes());
        dto.setLocation(task.getLocation());
        dto.setIsRecurring(task.getIsRecurring());
        dto.setRecurrenceType(task.getRecurrenceType());
        dto.setRecurrenceEndDate(task.getRecurrenceEndDate());
        dto.setParentTaskId(task.getParentTaskId());
        dto.setOrderIndex(task.getOrderIndex());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());

        // Convertir JSON strings a listas
        dto.setTags(parseJsonStringToList(task.getTags()));
        dto.setAttachmentUrls(parseJsonStringToList(task.getAttachmentUrls()));

        // Campos calculados
        dto.setIsOverdue(task.isOverdue());
        dto.setDaysUntilDue(calculateDaysUntilDue(task.getDueDate()));
        dto.setStatusDisplayName(task.getStatus() != null ? task.getStatus().getDisplayName() : null);
        dto.setPriorityDisplayName(task.getPriority() != null ? task.getPriority().getDisplayName() : null);
        dto.setCategoryDisplayName(task.getCategory() != null ? task.getCategory().getDisplayName() : null);

        return dto;
    }

    public List<TaskDTO> toDTOList(List<Task> tasks) {
        return tasks.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Task fromCreateDTO(CreateTaskDTO createTaskDTO) {
        if (createTaskDTO == null) {
            return null;
        }

        Task task = new Task();
        task.setTitle(createTaskDTO.getTitle());
        task.setDescription(createTaskDTO.getDescription());
        task.setUserId(createTaskDTO.getUserId());
        task.setPriority(createTaskDTO.getPriority());
        task.setCategory(createTaskDTO.getCategory());
        task.setDueDate(createTaskDTO.getDueDate());
        task.setReminderDate(createTaskDTO.getReminderDate());
        task.setEstimatedDurationMinutes(createTaskDTO.getEstimatedDurationMinutes());
        task.setLocation(createTaskDTO.getLocation());
        task.setIsRecurring(createTaskDTO.getIsRecurring());
        task.setRecurrenceType(createTaskDTO.getRecurrenceType());
        task.setRecurrenceEndDate(createTaskDTO.getRecurrenceEndDate());
        task.setParentTaskId(createTaskDTO.getParentTaskId());
        task.setOrderIndex(createTaskDTO.getOrderIndex());

        // Convertir listas a JSON strings
        task.setTags(parseListToJsonString(createTaskDTO.getTags()));

        // Status inicial
        task.setStatus(TaskStatus.PENDING);

        return task;
    }

    public Task updateFromDTO(Task task, UpdateTaskDTO updateTaskDTO) {
        if (task == null || updateTaskDTO == null) {
            return task;
        }

        if (updateTaskDTO.getTitle() != null) {
            task.setTitle(updateTaskDTO.getTitle());
        }
        if (updateTaskDTO.getDescription() != null) {
            task.setDescription(updateTaskDTO.getDescription());
        }
        if (updateTaskDTO.getStatus() != null) {
            task.setStatus(updateTaskDTO.getStatus());
            // Si se marca como completada, establecer fecha
            if (updateTaskDTO.getStatus() == TaskStatus.COMPLETED && task.getCompletedDate() == null) {
                task.setCompletedDate(LocalDateTime.now());
            }
        }
        if (updateTaskDTO.getPriority() != null) {
            task.setPriority(updateTaskDTO.getPriority());
        }
        if (updateTaskDTO.getCategory() != null) {
            task.setCategory(updateTaskDTO.getCategory());
        }
        if (updateTaskDTO.getDueDate() != null) {
            task.setDueDate(updateTaskDTO.getDueDate());
        }
        if (updateTaskDTO.getReminderDate() != null) {
            task.setReminderDate(updateTaskDTO.getReminderDate());
        }
        if (updateTaskDTO.getEstimatedDurationMinutes() != null) {
            task.setEstimatedDurationMinutes(updateTaskDTO.getEstimatedDurationMinutes());
        }
        if (updateTaskDTO.getActualDurationMinutes() != null) {
            task.setActualDurationMinutes(updateTaskDTO.getActualDurationMinutes());
        }
        if (updateTaskDTO.getLocation() != null) {
            task.setLocation(updateTaskDTO.getLocation());
        }
        if (updateTaskDTO.getParentTaskId() != null) {
            task.setParentTaskId(updateTaskDTO.getParentTaskId());
        }
        if (updateTaskDTO.getOrderIndex() != null) {
            task.setOrderIndex(updateTaskDTO.getOrderIndex());
        }

        // Actualizar listas
        if (updateTaskDTO.getTags() != null) {
            task.setTags(parseListToJsonString(updateTaskDTO.getTags()));
        }
        if (updateTaskDTO.getAttachmentUrls() != null) {
            task.setAttachmentUrls(parseListToJsonString(updateTaskDTO.getAttachmentUrls()));
        }

        return task;
    }

    private List<String> parseJsonStringToList(String jsonString) {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            log.warn("Error parsing JSON string to list: {}", jsonString, e);
            return new ArrayList<>();
        }
    }

    private String parseListToJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            log.warn("Error parsing list to JSON string: {}", list, e);
            return null;
        }
    }

    private Long calculateDaysUntilDue(LocalDateTime dueDate) {
        if (dueDate == null) {
            return null;
        }
        return ChronoUnit.DAYS.between(LocalDateTime.now(), dueDate);
    }
}