package com.microservice.microservice_task.service;

import com.microservice.microservice_task.assembler.TaskAssembler;
import com.microservice.microservice_task.dto.*;
import com.microservice.microservice_task.exception.*;
import com.microservice.microservice_task.model.Task;
import com.microservice.microservice_task.model.TaskStatus;
import com.microservice.microservice_task.repository.TaskRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskAssembler taskAssembler;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskAssembler taskAssembler) {
        this.taskRepository = taskRepository;
        this.taskAssembler = taskAssembler;
    }

    // ============= CRUD BÁSICO =============

    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasksByUser(UUID userId) {
        log.debug("Obteniendo todas las tareas del usuario: {}", userId);
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return taskAssembler.toDTOList(tasks);
    }

    @Transactional(readOnly = true)
    public Page<TaskDTO> getAllTasksByUser(UUID userId, Pageable pageable) {
        log.debug("Obteniendo tareas paginadas del usuario: {}", userId);
        Page<Task> tasks = taskRepository.findByUserIdOrderByOrderIndexAscCreatedAtDesc(userId, pageable);
        return tasks.map(taskAssembler::toDTO);
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskById(UUID taskId) {
        log.debug("Obteniendo tarea por ID: {}", taskId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));
        return taskAssembler.toDTO(task);
    }

    @Transactional(readOnly = true)
    public TaskDTO getTaskByIdAndUser(UUID taskId, UUID userId) {
        log.debug("Obteniendo tarea {} del usuario {}", taskId, userId);
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));
        
        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedTaskAccessException("No tienes permiso para acceder a esta tarea");
        }
        
        return taskAssembler.toDTO(task);
    }

    public TaskDTO createTask(CreateTaskDTO createTaskDTO) {
        log.debug("Creando nueva tarea para usuario: {}", createTaskDTO.getUserId());
        
        // Validar datos
        validateCreateTaskDTO(createTaskDTO);
        
        Task task = taskAssembler.fromCreateDTO(createTaskDTO);
        
        // Establecer orden si no se especifica
        if (task.getOrderIndex() == null || task.getOrderIndex() == 0) {
            task.setOrderIndex(getNextOrderIndex(createTaskDTO.getUserId()));
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Tarea creada exitosamente con ID: {}", savedTask.getId());
        
        return taskAssembler.toDTO(savedTask);
    }

    public TaskDTO updateTask(UUID taskId, UpdateTaskDTO updateTaskDTO, UUID userId) {
        log.debug("Actualizando tarea {} del usuario {}", taskId, userId);
        
        Task existingTask = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));
        
        if (!existingTask.getUserId().equals(userId)) {
            throw new UnauthorizedTaskAccessException("No tienes permiso para actualizar esta tarea");
        }
        
        Task updatedTask = taskAssembler.updateFromDTO(existingTask, updateTaskDTO);
        Task savedTask = taskRepository.save(updatedTask);
        
        log.info("Tarea actualizada exitosamente: {}", taskId);
        return taskAssembler.toDTO(savedTask);
    }

    public void deleteTask(UUID taskId, UUID userId) {
        log.debug("Eliminando tarea {} del usuario {}", taskId, userId);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));
        
        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedTaskAccessException("No tienes permiso para eliminar esta tarea");
        }
        
        // Si tiene subtareas, también las eliminamos
        List<Task> subtasks = taskRepository.findByParentTaskIdOrderByOrderIndexAsc(taskId);
        if (!subtasks.isEmpty()) {
            log.debug("Eliminando {} subtareas de la tarea {}", subtasks.size(), taskId);
            taskRepository.deleteAll(subtasks);
        }
        
        taskRepository.delete(task);
        log.info("Tarea eliminada exitosamente: {}", taskId);
    }

    // ============= OPERACIONES ESPECÍFICAS =============

    public TaskDTO completeTask(UUID taskId, UUID userId) {
        log.debug("Marcando tarea {} como completada para usuario {}", taskId, userId);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));
        
        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedTaskAccessException("No tienes permiso para completar esta tarea");
        }
        
        task.markAsCompleted();
        Task savedTask = taskRepository.save(task);
        
        log.info("Tarea completada exitosamente: {}", taskId);
        return taskAssembler.toDTO(savedTask);
    }

    public TaskDTO updateTaskStatus(UUID taskId, TaskStatus status, UUID userId) {
        log.debug("Actualizando status de tarea {} a {} para usuario {}", taskId, status, userId);
        
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskNotFoundException("Tarea no encontrada con ID: " + taskId));
        
        if (!task.getUserId().equals(userId)) {
            throw new UnauthorizedTaskAccessException("No tienes permiso para actualizar esta tarea");
        }
        
        task.setStatus(status);
        if (status == TaskStatus.COMPLETED && task.getCompletedDate() == null) {
            task.setCompletedDate(LocalDateTime.now());
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Status de tarea actualizado exitosamente: {} -> {}", taskId, status);
        
        return taskAssembler.toDTO(savedTask);
    }

    // ============= CONSULTAS ESPECÍFICAS =============

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByStatus(UUID userId, TaskStatus status) {
        log.debug("Obteniendo tareas con status {} del usuario {}", status, userId);
        List<Task> tasks = taskRepository.findByUserIdAndStatusOrderByDueDateAsc(userId, status);
        return taskAssembler.toDTOList(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTodayTasks(UUID userId) {
        log.debug("Obteniendo tareas de hoy del usuario {}", userId);
        LocalDateTime today = LocalDateTime.now();
        List<Task> tasks = taskRepository.findByUserIdAndDueDateToday(userId, today);
        return taskAssembler.toDTOList(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getOverdueTasks(UUID userId) {
        log.debug("Obteniendo tareas vencidas del usuario {}", userId);
        List<Task> tasks = taskRepository.findOverdueTasksByUserId(userId, LocalDateTime.now());
        return taskAssembler.toDTOList(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksThisWeek(UUID userId) {
        log.debug("Obteniendo tareas de esta semana del usuario {}", userId);
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        
        List<Task> tasks = taskRepository.findByUserIdAndDueDateBetweenOrderByDueDateAsc(
                userId, startOfWeek, endOfWeek);
        return taskAssembler.toDTOList(tasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> getSubtasks(UUID parentTaskId) {
        log.debug("Obteniendo subtareas de la tarea {}", parentTaskId);
        List<Task> subtasks = taskRepository.findByParentTaskIdOrderByOrderIndexAsc(parentTaskId);
        return taskAssembler.toDTOList(subtasks);
    }

    @Transactional(readOnly = true)
    public List<TaskDTO> searchTasks(UUID userId, String searchText) {
        log.debug("Buscando tareas del usuario {} con texto: {}", userId, searchText);
        List<Task> tasks = taskRepository.findByUserIdAndSearchText(userId, searchText);
        return taskAssembler.toDTOList(tasks);
    }

    @Transactional(readOnly = true)
    public Page<TaskDTO> getTasksWithFilters(TaskFilterDTO filterDTO, Pageable pageable) {
        log.debug("Obteniendo tareas con filtros para usuario {}", filterDTO.getUserId());
        
        LocalDateTime dueDateFrom = filterDTO.getDueDateFrom() != null ? 
                filterDTO.getDueDateFrom().atStartOfDay() : null;
        LocalDateTime dueDateTo = filterDTO.getDueDateTo() != null ? 
                filterDTO.getDueDateTo().atTime(23, 59, 59) : null;
        
        // Por simplicidad, tomamos solo el primer valor de cada lista
        TaskStatus status = filterDTO.getStatuses() != null && !filterDTO.getStatuses().isEmpty() ? 
                filterDTO.getStatuses().get(0) : null;
        
        Page<Task> tasks = taskRepository.findTasksWithFilters(
                filterDTO.getUserId(),
                status,
                filterDTO.getPriorities() != null && !filterDTO.getPriorities().isEmpty() ? 
                        filterDTO.getPriorities().get(0) : null,
                filterDTO.getCategories() != null && !filterDTO.getCategories().isEmpty() ? 
                        filterDTO.getCategories().get(0) : null,
                dueDateFrom,
                dueDateTo,
                filterDTO.getSearchText(),
                pageable
        );
        
        return tasks.map(taskAssembler::toDTO);
    }

    // ============= ESTADÍSTICAS =============

    @Transactional(readOnly = true)
    public TaskSummaryDTO getTaskSummary(UUID userId) {
        log.debug("Generando resumen de tareas para usuario {}", userId);
        
        Long totalTasks = taskRepository.countByUserId(userId);
        Long pendingTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
        Long inProgressTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.IN_PROGRESS);
        Long completedTasks = taskRepository.countByUserIdAndStatus(userId, TaskStatus.COMPLETED);
        Long overdueTasks = taskRepository.countOverdueByUserId(userId, LocalDateTime.now());
        Long todayTasks = taskRepository.countByUserIdAndDueToday(userId, LocalDateTime.now());
        
        // Tareas de esta semana
        LocalDateTime startOfWeek = LocalDate.now().atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);
        List<Task> weekTasks = taskRepository.findByUserIdAndDueDateBetweenOrderByDueDateAsc(
                userId, startOfWeek, endOfWeek);
        Long thisWeekTasks = (long) weekTasks.size();
        
        // Tasa de completitud
        Double completionRate = totalTasks > 0 ? 
                (completedTasks.doubleValue() / totalTasks.doubleValue()) * 100 : 0.0;
        
        // Duración promedio
        Double avgDuration = taskRepository.findAverageDurationByUserId(userId).orElse(0.0);
        
        return new TaskSummaryDTO(
                totalTasks,
                pendingTasks,
                inProgressTasks,
                completedTasks,
                overdueTasks,
                todayTasks,
                thisWeekTasks,
                completionRate,
                avgDuration.intValue()
        );
    }

    // ============= OPERACIONES MASIVAS =============

    public void deleteAllUserTasks(UUID userId) {
        log.debug("Eliminando todas las tareas del usuario {}", userId);
        taskRepository.deleteByUserId(userId);
        log.info("Todas las tareas del usuario {} han sido eliminadas", userId);
    }

    @Transactional(readOnly = true)
    public Long getTasksCount(UUID userId) {
        return taskRepository.countByUserId(userId);
    }

    // ============= MÉTODOS PRIVADOS =============

    private void validateCreateTaskDTO(CreateTaskDTO createTaskDTO) {
        if (createTaskDTO.getTitle() == null || createTaskDTO.getTitle().trim().isEmpty()) {
            throw new InvalidTaskDataException("El título de la tarea es requerido");
        }
        
        if (createTaskDTO.getUserId() == null) {
            throw new InvalidTaskDataException("El ID del usuario es requerido");
        }
        
        // Validar fechas
        if (createTaskDTO.getDueDate() != null && createTaskDTO.getReminderDate() != null) {
            if (createTaskDTO.getReminderDate().isAfter(createTaskDTO.getDueDate())) {
                throw new InvalidTaskDataException("La fecha de recordatorio no puede ser posterior a la fecha de vencimiento");
            }
        }
        
        // Validar recurrencia
        if (createTaskDTO.getIsRecurring() != null && createTaskDTO.getIsRecurring()) {
            if (createTaskDTO.getRecurrenceType() == null) {
                throw new InvalidTaskDataException("El tipo de recurrencia es requerido para tareas recurrentes");
            }
        }
    }

    private Integer getNextOrderIndex(UUID userId) {
        List<Task> userTasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return userTasks.isEmpty() ? 1 : 
               userTasks.stream()
                       .mapToInt(task -> task.getOrderIndex() != null ? task.getOrderIndex() : 0)
                       .max()
                       .orElse(0) + 1;
    }
}