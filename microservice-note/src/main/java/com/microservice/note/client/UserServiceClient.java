package com.microservice.note.client;

import com.microservice.note.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

// 'msvc-user' es el nombre con el que el servicio de usuarios se registró en Eureka
@FeignClient(name = "msvc-user", path = "/api/users")
public interface UserServiceClient {

    @GetMapping("/{id}")
    UserDTO getUserById(@PathVariable("id") UUID id);
}