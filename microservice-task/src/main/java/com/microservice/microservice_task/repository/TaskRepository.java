package com.microservice.microservice_task.repository;

import com.microservice.microservice_task.model.Task;
import com.microservice.microservice_task.model.TaskPriority;
import com.microservice.microservice_task.model.TaskStatus;
import com.microservice.microservice_task.model.TaskCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {

    // Consultas básicas por usuario
    List<Task> findByUserIdOrderByCreatedAtDesc(UUID userId);
    
    List<Task> findByUserIdAndStatusOrderByDueDateAsc(UUID userId, TaskStatus status);
    
    Page<Task> findByUserIdOrderByOrderIndexAscCreatedAtDesc(UUID userId, Pageable pageable);

    // Consultas por estado
    List<Task> findByUserIdAndStatus(UUID userId, TaskStatus status);
    
    List<Task> findByUserIdAndStatusIn(UUID userId, List<TaskStatus> statuses);

    // Consultas por prioridad
    List<Task> findByUserIdAndPriorityOrderByDueDateAsc(UUID userId, TaskPriority priority);

    // Consultas por categoría
    List<Task> findByUserIdAndCategoryOrderByCreatedAtDesc(UUID userId, TaskCategory category);

    // Consultas por fecha
    List<Task> findByUserIdAndDueDateBetweenOrderByDueDateAsc(UUID userId, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND DATE(t.dueDate) = DATE(:date) ORDER BY t.dueDate ASC")
    List<Task> findByUserIdAndDueDateToday(@Param("userId") UUID userId, @Param("date") LocalDateTime date);

    // Tareas vencidas
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND t.dueDate < :now AND t.status != 'COMPLETED' ORDER BY t.dueDate ASC")
    List<Task> findOverdueTasksByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    // Tareas recurrentes
    List<Task> findByUserIdAndIsRecurringTrue(UUID userId);

    // Subtareas
    List<Task> findByParentTaskIdOrderByOrderIndexAsc(UUID parentTaskId);
    
    List<Task> findByUserIdAndParentTaskIdIsNullOrderByOrderIndexAscCreatedAtDesc(UUID userId);

    // Búsqueda de texto
    @Query("SELECT t FROM Task t WHERE t.userId = :userId AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(t.tags) LIKE LOWER(CONCAT('%', :searchText, '%')))")
    List<Task> findByUserIdAndSearchText(@Param("userId") UUID userId, @Param("searchText") String searchText);

    // Consulta compleja con filtros
    @Query("SELECT t FROM Task t WHERE t.userId = :userId " +
           "AND (:status IS NULL OR t.status = :status) " +
           "AND (:priority IS NULL OR t.priority = :priority) " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND (:dueDateFrom IS NULL OR t.dueDate >= :dueDateFrom) " +
           "AND (:dueDateTo IS NULL OR t.dueDate <= :dueDateTo) " +
           "AND (:searchText IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "     OR LOWER(t.description) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
           "ORDER BY t.orderIndex ASC, t.createdAt DESC")
    Page<Task> findTasksWithFilters(@Param("userId") UUID userId,
                                  @Param("status") TaskStatus status,
                                  @Param("priority") TaskPriority priority,
                                  @Param("category") TaskCategory category,
                                  @Param("dueDateFrom") LocalDateTime dueDateFrom,
                                  @Param("dueDateTo") LocalDateTime dueDateTo,
                                  @Param("searchText") String searchText,
                                  Pageable pageable);

    // Estadísticas
    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId")
    Long countByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.status = :status")
    Long countByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") TaskStatus status);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND t.dueDate < :now AND t.status != 'COMPLETED'")
    Long countOverdueByUserId(@Param("userId") UUID userId, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.userId = :userId AND DATE(t.dueDate) = DATE(:date)")
    Long countByUserIdAndDueToday(@Param("userId") UUID userId, @Param("date") LocalDateTime date);

    @Query("SELECT AVG(t.actualDurationMinutes) FROM Task t WHERE t.userId = :userId AND t.actualDurationMinutes IS NOT NULL")
    Optional<Double> findAverageDurationByUserId(@Param("userId") UUID userId);

    // Eliminar tareas del usuario
    void deleteByUserId(UUID userId);

    // Verificar si existe tarea
    boolean existsByUserIdAndId(UUID userId, UUID taskId);
}