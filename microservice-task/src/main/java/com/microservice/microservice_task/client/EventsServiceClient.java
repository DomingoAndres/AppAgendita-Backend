package com.microservice.microservice_task.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "microservice-events", path = "/api/events")
public interface EventsServiceClient {

    @PostMapping("/from-task")
    Object createEventFromTask(@RequestBody Object taskEventData);

    @GetMapping("/user/{userId}/conflicts")
    List<Object> getConflictingEvents(@PathVariable("userId") UUID userId, @RequestParam String dateTime);
}