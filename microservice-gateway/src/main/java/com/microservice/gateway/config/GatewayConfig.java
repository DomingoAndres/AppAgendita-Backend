package com.microservice.gateway.config;

import com.microservice.gateway.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    // Inyectamos el filtro por si lo usas en el futuro, pero no definimos rutas aquí.
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /* COMENTAMOS TODO ESTO PARA QUE MANDE EL APPLICATION.YML
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                ... (todo tu código antiguo) ...
                .build();
    }
    */
}