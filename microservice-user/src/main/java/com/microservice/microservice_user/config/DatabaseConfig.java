package com.microservice.microservice_user.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.microservice.microservice_user.repository")
@EnableJpaAuditing
public class DatabaseConfig {
}