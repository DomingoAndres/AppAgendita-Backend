package com.microservice.microservice_task.controller;

import com.microservice.microservice_task.dto.*;
import com.microservice.microservice_task.model.TaskStatus;
import com.microservice.microservice_task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "API para gestión de tareas en AppAgendita")
@Slf4j
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // ============= CRUD BÁSICO =============

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtener todas las tareas de un usuario")
    public ResponseEntity<List<TaskDTO>> getAllTasksByUser(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/user/{}", userId);
        List<TaskDTO> tasks = taskService.getAllTasksByUser(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}/pageable")
    @Operation(summary = "Obtener tareas de un usuario con paginación")
    public ResponseEntity<Page<TaskDTO>> getAllTasksByUserPageable(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            Pageable pageable) {
        log.debug("GET /api/tasks/user/{}/pageable", userId);
        Page<TaskDTO> tasks = taskService.getAllTasksByUser(userId, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{taskId}")
    @Operation(summary = "Obtener tarea por ID")
    public ResponseEntity<TaskDTO> getTaskById(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId) {
        log.debug("GET /api/tasks/{}", taskId);
        TaskDTO task = taskService.getTaskById(taskId);
        return ResponseEntity.ok(task);
    }

    @GetMapping("/{taskId}/user/{userId}")
    @Operation(summary = "Obtener tarea específica de un usuario")
    public ResponseEntity<TaskDTO> getTaskByIdAndUser(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/{}/user/{}", taskId, userId);
        TaskDTO task = taskService.getTaskByIdAndUser(taskId, userId);
        return ResponseEntity.ok(task);
    }

    @PostMapping
    @Operation(summary = "Crear nueva tarea")
    public ResponseEntity<TaskDTO> createTask(
            @Parameter(description = "Datos para crear la tarea") @Valid @RequestBody CreateTaskDTO createTaskDTO) {
        log.debug("POST /api/tasks - Usuario: {}", createTaskDTO.getUserId());
        TaskDTO createdTask = taskService.createTask(createTaskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @PutMapping("/{taskId}/user/{userId}")
    @Operation(summary = "Actualizar tarea")
    public ResponseEntity<TaskDTO> updateTask(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Datos para actualizar la tarea") @Valid @RequestBody UpdateTaskDTO updateTaskDTO) {
        log.debug("PUT /api/tasks/{}/user/{}", taskId, userId);
        TaskDTO updatedTask = taskService.updateTask(taskId, updateTaskDTO, userId);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{taskId}/user/{userId}")
    @Operation(summary = "Eliminar tarea")
    public ResponseEntity<Map<String, String>> deleteTask(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("DELETE /api/tasks/{}/user/{}", taskId, userId);
        taskService.deleteTask(taskId, userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Tarea eliminada exitosamente");
        return ResponseEntity.ok(response);
    }

    // ============= OPERACIONES ESPECÍFICAS =============

    @PutMapping("/{taskId}/complete/user/{userId}")
    @Operation(summary = "Marcar tarea como completada")
    public ResponseEntity<TaskDTO> completeTask(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("PUT /api/tasks/{}/complete/user/{}", taskId, userId);
        TaskDTO completedTask = taskService.completeTask(taskId, userId);
        return ResponseEntity.ok(completedTask);
    }

    @PutMapping("/{taskId}/status/user/{userId}")
    @Operation(summary = "Actualizar estado de la tarea")
    public ResponseEntity<TaskDTO> updateTaskStatus(
            @Parameter(description = "ID de la tarea") @PathVariable UUID taskId,
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Nuevo estado") @RequestParam TaskStatus status) {
        log.debug("PUT /api/tasks/{}/status/user/{} - Status: {}", taskId, userId, status);
        TaskDTO updatedTask = taskService.updateTaskStatus(taskId, status, userId);
        return ResponseEntity.ok(updatedTask);
    }

    // ============= CONSULTAS ESPECÍFICAS =============

    @GetMapping("/user/{userId}/status/{status}")
    @Operation(summary = "Obtener tareas por estado")
    public ResponseEntity<List<TaskDTO>> getTasksByStatus(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Estado de las tareas") @PathVariable TaskStatus status) {
        log.debug("GET /api/tasks/user/{}/status/{}", userId, status);
        List<TaskDTO> tasks = taskService.getTasksByStatus(userId, status);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}/today")
    @Operation(summary = "Obtener tareas de hoy")
    public ResponseEntity<List<TaskDTO>> getTodayTasks(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/user/{}/today", userId);
        List<TaskDTO> tasks = taskService.getTodayTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}/overdue")
    @Operation(summary = "Obtener tareas vencidas")
    public ResponseEntity<List<TaskDTO>> getOverdueTasks(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/user/{}/overdue", userId);
        List<TaskDTO> tasks = taskService.getOverdueTasks(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/user/{userId}/week")
    @Operation(summary = "Obtener tareas de esta semana")
    public ResponseEntity<List<TaskDTO>> getTasksThisWeek(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/user/{}/week", userId);
        List<TaskDTO> tasks = taskService.getTasksThisWeek(userId);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{parentTaskId}/subtasks")
    @Operation(summary = "Obtener subtareas")
    public ResponseEntity<List<TaskDTO>> getSubtasks(
            @Parameter(description = "ID de la tarea padre") @PathVariable UUID parentTaskId) {
        log.debug("GET /api/tasks/{}/subtasks", parentTaskId);
        List<TaskDTO> subtasks = taskService.getSubtasks(parentTaskId);
        return ResponseEntity.ok(subtasks);
    }

    @GetMapping("/user/{userId}/search")
    @Operation(summary = "Buscar tareas")
    public ResponseEntity<List<TaskDTO>> searchTasks(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Texto a buscar") @RequestParam String searchText) {
        log.debug("GET /api/tasks/user/{}/search?searchText={}", userId, searchText);
        List<TaskDTO> tasks = taskService.searchTasks(userId, searchText);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/user/{userId}/filter")
    @Operation(summary = "Obtener tareas con filtros")
    public ResponseEntity<Page<TaskDTO>> getTasksWithFilters(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId,
            @Parameter(description = "Filtros a aplicar") @RequestBody TaskFilterDTO filterDTO,
            Pageable pageable) {
        log.debug("POST /api/tasks/user/{}/filter", userId);
        filterDTO.setUserId(userId); // Asegurar que el userId coincida con el path
        Page<TaskDTO> tasks = taskService.getTasksWithFilters(filterDTO, pageable);
        return ResponseEntity.ok(tasks);
    }

    // ============= ESTADÍSTICAS =============

    @GetMapping("/user/{userId}/summary")
    @Operation(summary = "Obtener resumen de tareas del usuario")
    public ResponseEntity<TaskSummaryDTO> getTaskSummary(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/user/{}/summary", userId);
        TaskSummaryDTO summary = taskService.getTaskSummary(userId);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Obtener cantidad total de tareas del usuario")
    public ResponseEntity<Map<String, Long>> getTasksCount(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("GET /api/tasks/user/{}/count", userId);
        Long count = taskService.getTasksCount(userId);
        
        Map<String, Long> response = new HashMap<>();
        response.put("totalTasks", count);
        return ResponseEntity.ok(response);
    }

    // ============= OPERACIONES MASIVAS =============

    @DeleteMapping("/user/{userId}")
    @Operation(summary = "Eliminar todas las tareas de un usuario")
    public ResponseEntity<Map<String, String>> deleteAllUserTasks(
            @Parameter(description = "ID del usuario") @PathVariable UUID userId) {
        log.debug("DELETE /api/tasks/user/{}", userId);
        taskService.deleteAllUserTasks(userId);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Todas las tareas del usuario han sido eliminadas");
        return ResponseEntity.ok(response);
    }
}