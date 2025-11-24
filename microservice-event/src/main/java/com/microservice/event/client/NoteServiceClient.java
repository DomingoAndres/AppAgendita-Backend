package com.microservice.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(name = "microservice-note")
@RequestMapping("/notes")
public interface NoteServiceClient {

    @GetMapping("/{id}/exists")
    boolean noteExists(@PathVariable("id") String id);

    // Add other necessary note metadata retrieval methods
}
