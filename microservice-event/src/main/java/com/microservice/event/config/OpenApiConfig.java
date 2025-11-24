package com.microservice.event.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI microserviceEventOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Microservice Event API")
                .description("API documentation for the Microservice Event")
                .version("v1.0"))
                .externalDocs(new ExternalDocumentation()
                .description("Microservice Event Wiki Documentation")
                .url("https://example.com/docs"));
    }

    @Bean
    public GroupedOpenApi eventApi() {            // 👈 usa el import, no el FQN viejo
        return GroupedOpenApi.builder()
                .group("event")
                .pathsToMatch("/events/**")
                .build();
    }
}
