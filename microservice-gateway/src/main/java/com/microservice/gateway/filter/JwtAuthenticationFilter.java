package com.microservice.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Filtro JWT para validación centralizada de tokens en el Gateway
 * Este filtro intercepta las peticiones y valida el token JWT antes de reenviar al microservicio
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    @Value("${jwt.secret:myVerySecretKeyForAppAgenditaUserService2024}")
    private String jwtSecret;

    // Lista de rutas públicas que no requieren autenticación
    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/users", // POST para registro también
            "/actuator",
            "/swagger-ui",
            "/v3/api-docs"
    );

    public JwtAuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String path = request.getURI().getPath();

            // Permitir rutas públicas sin validación de token
            if (isPublicPath(path)) {
                log.debug("Ruta pública detectada: {}", path);
                return chain.filter(exchange);
            }

            // Verificar que exista el header Authorization
            if (!request.getHeaders().containsKey("Authorization")) {
                log.warn("Token JWT no encontrado en la petición a: {}", path);
                return onError(exchange, "Token de autorización no encontrado", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst("Authorization");
            
            // Verificar formato Bearer token
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Formato de token inválido en: {}", path);
                return onError(exchange, "Formato de token inválido", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7); // Remover "Bearer "

            try {
                // Validar el token
                validateToken(token);
                
                // Extraer información del usuario y añadirla al header
                Claims claims = extractClaims(token);
                String userId = claims.get("userId", String.class);
                String username = claims.getSubject();

                // Añadir información del usuario a los headers para que los microservicos la usen
                ServerHttpRequest modifiedRequest = request.mutate()
                        .header("X-User-Id", userId)
                        .header("X-Username", username)
                        .build();

                log.debug("Token JWT válido para usuario: {} ({})", username, userId);
                
                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("Error al validar token JWT: {}", e.getMessage());
                return onError(exchange, "Token inválido o expirado", HttpStatus.UNAUTHORIZED);
            }
        };
    }

    /**
     * Valida si el token JWT es válido
     */
    private void validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    /**
     * Extrae los claims del token
     */
    private Claims extractClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si la ruta es pública y no requiere autenticación
     */
    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(publicPath -> path.startsWith(publicPath));
    }

    /**
     * Maneja errores de autenticación devolviendo una respuesta JSON
     */
    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");
        
        String errorResponse = String.format(
            "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\"}",
            java.time.LocalDateTime.now(),
            httpStatus.value(),
            httpStatus.getReasonPhrase(),
            message
        );
        
        return response.writeWith(
            Mono.just(response.bufferFactory().wrap(errorResponse.getBytes(StandardCharsets.UTF_8)))
        );
    }

    public static class Config {
        // Configuración del filtro si es necesaria
    }
}
