package com.microservice.microservice_task.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "microservice-notifications", path = "/api/notifications")
public interface NotificationServiceClient {

    @PostMapping("/task-reminder")
    Boolean sendTaskReminder(@RequestBody Object reminderData);

    @PostMapping("/task-overdue")
    Boolean sendOverdueNotification(@RequestBody Object overdueData);

    @PostMapping("/task-completed")
    Boolean sendTaskCompletedNotification(@RequestBody Object completionData);
}