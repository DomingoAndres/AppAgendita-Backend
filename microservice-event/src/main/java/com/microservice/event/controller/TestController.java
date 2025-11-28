package com.microservice.event.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/events/test")
    public String test() {
        return "microservice-event OK";
    }
}
