package com.microservice.microservice_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "microservice-tasks", path = "/api/tasks") //verificar si en vez del path deberia venir el localhost del otro microservicio
public interface TaskServiceClient {

    @GetMapping("/user/{userId}")
    List<Object> getUserTasks(@PathVariable("userId") UUID userId);

    @DeleteMapping("/user/{userId}")
    Boolean deleteUserTasks(@PathVariable("userId") UUID userId);

    @GetMapping("/{taskId}")
    Object getTaskById(@PathVariable("taskId") UUID taskId);

    @PostMapping
    Object createTask(@RequestBody Object taskDTO);

    @PutMapping("/{taskId}")
    Object updateTask(@PathVariable("taskId") UUID taskId, @RequestBody Object taskDTO);

    @DeleteMapping("/{taskId}")
    Boolean deleteTask(@PathVariable("taskId") UUID taskId);

    @GetMapping("/user/{userId}/completed")
    List<Object> getUserCompletedTasks(@PathVariable("userId") UUID userId);

    @GetMapping("/user/{userId}/pending")
    List<Object> getUserPendingTasks(@PathVariable("userId") UUID userId);

    @PutMapping("/{taskId}/complete")
    Boolean completeTask(@PathVariable("taskId") UUID taskId);

    @GetMapping("/user/{userId}/count")
    Long getUserTasksCount(@PathVariable("userId") UUID userId);
}