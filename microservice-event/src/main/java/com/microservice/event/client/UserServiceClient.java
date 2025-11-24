package com.microservice.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "microservice-user")
@RequestMapping("/users")
public interface UserServiceClient {

    @GetMapping("/{id}/exists")
    boolean userExists(@PathVariable("id") String id);

    // Add other necessary user validation methods as needed
}
