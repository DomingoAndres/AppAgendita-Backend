package com.microservice.microservice_task.controller;

import com.microservice.microservice_task.repository.TaskRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Tag(name = "Test", description = "Endpoints de prueba para el microservicio de tareas")
public class TestController {

    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/health")
    @Operation(summary = "Verificar salud del microservicio")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "microservice-tasks");
        health.put("totalTasks", taskRepository.count());
        health.put("database", "MySQL via Laragon");
        health.put("version", "1.0.0");
        
        return health;
    }
}