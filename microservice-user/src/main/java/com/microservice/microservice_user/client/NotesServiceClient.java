package com.microservice.microservice_user.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "microservice-notes", path = "/api/notes") //verificar si en vez del path deberia venir el localhost del otro microservicio
public interface NotesServiceClient {

    @GetMapping("/user/{userId}")
    List<Object> getUserNotes(@PathVariable("userId") UUID userId);

    @DeleteMapping("/user/{userId}")
    Boolean deleteUserNotes(@PathVariable("userId") UUID userId);

    @GetMapping("/{noteId}")
    Object getNoteById(@PathVariable("noteId") UUID noteId);

    @PostMapping
    Object createNote(@RequestBody Object noteDTO);

    @PutMapping("/{noteId}")
    Object updateNote(@PathVariable("noteId") UUID noteId, @RequestBody Object noteDTO);

    @DeleteMapping("/{noteId}")
    Boolean deleteNote(@PathVariable("noteId") UUID noteId);

    @GetMapping("/user/{userId}/count")
    Long getUserNotesCount(@PathVariable("userId") UUID userId);
}