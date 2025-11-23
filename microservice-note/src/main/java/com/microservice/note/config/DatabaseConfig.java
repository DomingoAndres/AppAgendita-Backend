package com.microservice.note.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
// Indica dónde están los repositorios de este microservicio
@EnableJpaRepositories(basePackages = "com.microservice.note.repository")
// Activa la auditoría para que @CreatedDate funcione en la entidad Note
@EnableJpaAuditing
public class DatabaseConfig {
}