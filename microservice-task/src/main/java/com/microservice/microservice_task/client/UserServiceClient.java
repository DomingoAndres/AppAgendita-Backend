package com.microservice.microservice_task.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "microservice-user", path = "/api/users")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    Object getUserById(@PathVariable("userId") UUID userId);

    @GetMapping("/{userId}/exists")
    Boolean userExists(@PathVariable("userId") UUID userId);

    @GetMapping("/{userId}/active")
    Boolean isUserActive(@PathVariable("userId") UUID userId);
}