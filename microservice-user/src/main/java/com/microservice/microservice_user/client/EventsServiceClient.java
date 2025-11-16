package com.microservice.microservice_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "microservice-events", path = "/api/events") //verificar si en vez del path deberia venir el localhost del otro microservicio
public interface EventsServiceClient {

    @GetMapping("/user/{userId}")
    List<Object> getUserEvents(@PathVariable("userId") UUID userId);

    @DeleteMapping("/user/{userId}")
    Boolean deleteUserEvents(@PathVariable("userId") UUID userId);

    @GetMapping("/{eventId}")
    Object getEventById(@PathVariable("eventId") UUID eventId);

    @PostMapping
    Object createEvent(@RequestBody Object eventDTO);

    @PutMapping("/{eventId}")
    Object updateEvent(@PathVariable("eventId") UUID eventId, @RequestBody Object eventDTO);

    @DeleteMapping("/{eventId}")
    Boolean deleteEvent(@PathVariable("eventId") UUID eventId);

    @GetMapping("/user/{userId}/upcoming")
    List<Object> getUserUpcomingEvents(@PathVariable("userId") UUID userId);

    @GetMapping("/user/{userId}/past")
    List<Object> getUserPastEvents(@PathVariable("userId") UUID userId);

    @GetMapping("/user/{userId}/count")
    Long getUserEventsCount(@PathVariable("userId") UUID userId);
}