package com.microservice.microservice_task;

import com.microservice.microservice_task.model.*;
import com.microservice.microservice_task.repository.TaskRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner initTaskDatabase(TaskRepository taskRepository) {
        return args -> {
            if (taskRepository.count() == 0L) {
                
                System.out.println("🚀 Iniciando carga de datos de prueba para TAREAS...");
                
                List<Task> tasks = new ArrayList<>();
                Faker faker = new Faker(new Locale("es"));
                
                // UUIDs de usuarios ficticios (deberían coincidir con los del microservice-user)
                UUID user1Id = UUID.fromString("123e4567-e89b-1234-5678-123456789001");
                UUID user2Id = UUID.fromString("123e4567-e89b-1234-5678-123456789002");
                UUID user3Id = UUID.fromString("123e4567-e89b-1234-5678-123456789003");
                
                // Tareas para usuario 1
                tasks.addAll(createTasksForUser(user1Id, faker, 8));
                
                // Tareas para usuario 2
                tasks.addAll(createTasksForUser(user2Id, faker, 6));
                
                // Tareas para usuario 3
                tasks.addAll(createTasksForUser(user3Id, faker, 4));
                
                // Guardar todas las tareas
                taskRepository.saveAll(tasks);
                
                System.out.println("✅ " + tasks.size() + " tareas creadas exitosamente!");
                System.out.println("📋 Tareas de diferentes categorías y prioridades");
                System.out.println("🌐 Swagger UI: http://localhost:8071/doc/swagger-ui.html");
                System.out.println("💾 Base de datos: agendita_tasks_dev en MySQL");
                
            } else {
                System.out.println("⚡ Base de datos ya contiene " + taskRepository.count() + " tareas");
            }
        };
    }
    
    private List<Task> createTasksForUser(UUID userId, Faker faker, int count) {
        List<Task> tasks = new ArrayList<>();
        
        TaskCategory[] categories = TaskCategory.values();
        TaskPriority[] priorities = TaskPriority.values();
        TaskStatus[] statuses = TaskStatus.values();
        
        for (int i = 0; i < count; i++) {
            Task task = new Task();
            task.setTitle(generateTaskTitle(faker, i));
            task.setDescription(faker.lorem().paragraph(2));
            task.setUserId(userId);
            task.setStatus(statuses[faker.random().nextInt(statuses.length)]);
            task.setPriority(priorities[faker.random().nextInt(priorities.length)]);
            task.setCategory(categories[faker.random().nextInt(categories.length)]);
            
            // Fechas
            if (faker.bool().bool()) {
                task.setDueDate(LocalDateTime.now().plusDays(faker.random().nextInt(-5, 15)));
            }
            if (task.getDueDate() != null && faker.bool().bool()) {
                task.setReminderDate(task.getDueDate().minusHours(faker.random().nextInt(1, 24)));
            }
            
            // Duración
            if (faker.bool().bool()) {
                task.setEstimatedDurationMinutes(faker.random().nextInt(15, 480)); // 15 min a 8 horas
            }
            
            // Si está completada, agregar duración real y fecha
            if (task.getStatus() == TaskStatus.COMPLETED) {
                task.setCompletedDate(LocalDateTime.now().minusDays(faker.random().nextInt(0, 7)));
                if (task.getEstimatedDurationMinutes() != null) {
                    task.setActualDurationMinutes(
                        (int) (task.getEstimatedDurationMinutes() * (0.8 + faker.random().nextDouble() * 0.4))
                    );
                }
            }
            
            // Tags (algunos)
            if (faker.bool().bool()) {
                task.setTags("[\"" + faker.commerce().department().toLowerCase() + "\",\"" + 
                           (faker.bool().bool() ? "urgente" : "normal") + "\"]");
            }
            
            // Ubicación (algunos)
            if (faker.bool().bool()) {
                task.setLocation(faker.address().cityName());
            }
            
            // Orden
            task.setOrderIndex(i + 1);
            
            tasks.add(task);
        }
        
        return tasks;
    }
    
    private String generateTaskTitle(Faker faker, int index) {
        String[] taskTypes = {
            "Completar proyecto de", "Revisar informe de", "Reunión con", "Llamar a",
            "Comprar", "Organizar", "Planificar", "Estudiar", "Preparar presentación de",
            "Enviar email a", "Actualizar", "Configurar", "Instalar", "Reparar"
        };
        
        String taskType = taskTypes[faker.random().nextInt(taskTypes.length)];
        String subject = faker.company().buzzword();
        
        return taskType + " " + subject;
    }
}