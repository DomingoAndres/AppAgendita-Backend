package com.microservice.microservice_task.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.microservice.tasks.repository")
@EnableJpaAuditing
public class DatabaseConfig {
}