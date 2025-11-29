package com.microservice.gateway.config;

import com.microservice.gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas del Gateway con filtro JWT aplicado
 */
@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta para autenticación (públicas)
                .route("auth", r -> r
                        .path("/api/auth/**")
                           .uri("http://localhost:8080"))
                
                // Ruta para usuarios - SIN FILTRO JWT (desarrollo)
                .route("users", r -> r
                        .path("/api/users/**")
                        // .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                           .uri("http://localhost:8080"))
                
                // Ruta para tareas - SIN FILTRO JWT (desarrollo)
                .route("tasks", r -> r
                        .path("/api/tasks/**")
                        // .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:8071"))
                
                // Rutas para futuros microservicios
                .route("notes", r -> r
                        .path("/api/notes/**")
                        // .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:9090"))
                
                .route("events", r -> r
                        .path("/api/events/**")
                        // .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("http://localhost:9091"))
                
                .build();
    }
}
