package com.microservice.microservice_task.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AppAgendita - Tasks Service API")
                        .description("Microservicio de gestión de tareas para AppAgendita")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("AppAgendita Team")
                                .email("support@appagendita.com")
                        )
                );
    }
}