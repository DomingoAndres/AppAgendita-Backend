package com.microservice.event.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "microservice-user", path = "/api/users")
public interface UserServiceClient {

    @GetMapping("/{id}/exists")
    Boolean userExists(@PathVariable("id") UUID id);
}
