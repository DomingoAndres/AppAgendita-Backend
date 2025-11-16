package com.microservice.microservice_task.repository;

import com.microservice.microservice_task.model.Task;
import com.microservice.microservice_task.model.TaskCategory;
import com.microservice.microservice_task.model.TaskPriority;
import com.microservice.microservice_task.model.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    private UUID userId;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        // Limpiar datos existentes
        taskRepository.deleteAll();
        
        userId = UUID.randomUUID();

        task1 = new Task();
        task1.setUserId(userId);
        task1.setTitle("Tarea 1");
        task1.setDescription("Descripción 1");
        task1.setStatus(TaskStatus.PENDING);
        task1.setPriority(TaskPriority.HIGH);
        task1.setCategory(TaskCategory.WORK);
        task1.setDueDate(LocalDateTime.now().plusDays(1));
        task1.setOrderIndex(1);
        task1.setCreatedAt(LocalDateTime.now().minusMinutes(10));
        task1.setUpdatedAt(LocalDateTime.now().minusMinutes(10));

        task2 = new Task();
        task2.setUserId(userId);
        task2.setTitle("Tarea 2");
        task2.setDescription("Descripción 2");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        task2.setPriority(TaskPriority.MEDIUM);
        task2.setCategory(TaskCategory.PERSONAL);
        task2.setDueDate(LocalDateTime.now().plusDays(2));
        task2.setOrderIndex(2);
        task2.setCreatedAt(LocalDateTime.now().minusMinutes(5));
        task2.setUpdatedAt(LocalDateTime.now().minusMinutes(5));

        task3 = new Task();
        task3.setUserId(userId);
        task3.setTitle("Tarea vencida");
        task3.setDescription("Esta está vencida");
        task3.setStatus(TaskStatus.PENDING);
        task3.setPriority(TaskPriority.LOW);
        task3.setCategory(TaskCategory.WORK);
        task3.setDueDate(LocalDateTime.now().minusDays(1));
        task3.setOrderIndex(3);
        task3.setCreatedAt(LocalDateTime.now());
        task3.setUpdatedAt(LocalDateTime.now());

        task1 = taskRepository.save(task1);
        task2 = taskRepository.save(task2);
        task3 = taskRepository.save(task3);
    }

    @Test
    void testFindByUserIdOrderByCreatedAtDesc() {
        List<Task> tasks = taskRepository.findByUserIdOrderByCreatedAtDesc(userId);
        
        assertThat(tasks).hasSize(3);
        assertThat(tasks.get(0).getTitle()).isEqualTo("Tarea vencida");
    }

    @Test
    void testFindByUserIdAndStatus() {
        List<Task> todoTasks = taskRepository.findByUserIdAndStatus(userId, TaskStatus.PENDING);
        
        assertThat(todoTasks).hasSize(2);
        assertThat(todoTasks).extracting(Task::getStatus).containsOnly(TaskStatus.PENDING);
    }

    @Test
    void testFindByUserIdAndPriorityOrderByDueDateAsc() {
        List<Task> highPriorityTasks = taskRepository.findByUserIdAndPriorityOrderByDueDateAsc(userId, TaskPriority.HIGH);
        
        assertThat(highPriorityTasks).hasSize(1);
        assertThat(highPriorityTasks.get(0).getTitle()).isEqualTo("Tarea 1");
    }

    @Test
    void testFindByUserIdAndCategoryOrderByCreatedAtDesc() {
        List<Task> workTasks = taskRepository.findByUserIdAndCategoryOrderByCreatedAtDesc(userId, TaskCategory.WORK);
        
        assertThat(workTasks).hasSize(2);
        assertThat(workTasks).extracting(Task::getCategory).containsOnly(TaskCategory.WORK);
    }

    @Test
    void testFindOverdueTasksByUserId() {
        List<Task> overdueTasks = taskRepository.findOverdueTasksByUserId(userId, LocalDateTime.now());
        
        assertThat(overdueTasks).hasSize(1);
        assertThat(overdueTasks.get(0).getTitle()).isEqualTo("Tarea vencida");
    }

    @Test
    void testFindByUserIdOrderByOrderIndexAscCreatedAtDesc() {
        Page<Task> tasksPage = taskRepository.findByUserIdOrderByOrderIndexAscCreatedAtDesc(userId, PageRequest.of(0, 10));
        
        assertThat(tasksPage.getContent()).hasSize(3);
        assertThat(tasksPage.getContent().get(0).getOrderIndex()).isEqualTo(1);
    }

    @Test
    void testCountByUserId() {
        Long count = taskRepository.countByUserId(userId);
        
        assertThat(count).isEqualTo(3);
    }

    @Test
    void testCountByUserIdAndStatus() {
        Long todoCount = taskRepository.countByUserIdAndStatus(userId, TaskStatus.PENDING);
        
        assertThat(todoCount).isEqualTo(2);
    }

    @Test
    void testCountOverdueByUserId() {
        Long overdueCount = taskRepository.countOverdueByUserId(userId, LocalDateTime.now());
        
        assertThat(overdueCount).isEqualTo(1);
    }

    @Test
    void testExistsByUserIdAndId() {
        boolean exists = taskRepository.existsByUserIdAndId(userId, task1.getId());
        boolean notExists = taskRepository.existsByUserIdAndId(UUID.randomUUID(), task1.getId());
        
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void testFindByParentTaskIdOrderByOrderIndexAsc() {
        Task parentTask = new Task();
        parentTask.setUserId(userId);
        parentTask.setTitle("Tarea padre");
        parentTask.setStatus(TaskStatus.PENDING);
        parentTask.setPriority(TaskPriority.HIGH);
        parentTask.setCategory(TaskCategory.WORK);
        parentTask.setCreatedAt(LocalDateTime.now());
        parentTask.setUpdatedAt(LocalDateTime.now());
        parentTask = taskRepository.save(parentTask);

        Task subtask1 = new Task();
        subtask1.setUserId(userId);
        subtask1.setTitle("Subtarea 1");
        subtask1.setStatus(TaskStatus.PENDING);
        subtask1.setPriority(TaskPriority.MEDIUM);
        subtask1.setCategory(TaskCategory.WORK);
        subtask1.setParentTaskId(parentTask.getId());
        subtask1.setOrderIndex(1);
        subtask1.setCreatedAt(LocalDateTime.now());
        subtask1.setUpdatedAt(LocalDateTime.now());

        Task subtask2 = new Task();
        subtask2.setUserId(userId);
        subtask2.setTitle("Subtarea 2");
        subtask2.setStatus(TaskStatus.PENDING);
        subtask2.setPriority(TaskPriority.LOW);
        subtask2.setCategory(TaskCategory.WORK);
        subtask2.setParentTaskId(parentTask.getId());
        subtask2.setOrderIndex(2);
        subtask2.setCreatedAt(LocalDateTime.now());
        subtask2.setUpdatedAt(LocalDateTime.now());

        taskRepository.save(subtask1);
        taskRepository.save(subtask2);

        List<Task> subtasks = taskRepository.findByParentTaskIdOrderByOrderIndexAsc(parentTask.getId());
        
        assertThat(subtasks).hasSize(2);
        assertThat(subtasks.get(0).getOrderIndex()).isLessThan(subtasks.get(1).getOrderIndex());
    }
}
