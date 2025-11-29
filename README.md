spring.application.name=msvc-<servicio>
spring.config.import=optional:configserver:http://localhost:8888
spring.profiles.active=dev

# AppAgendita-Backend

## Descripción
Backend de la aplicación Agendita, implementado con arquitectura de microservicios usando Spring Boot, Spring Cloud, Eureka, Config Server y API Gateway. Cada microservicio gestiona una parte del dominio (usuarios, tareas, notas, eventos).

## Estructura del proyecto

- **microservice-config**: Config Server centralizado (provee configuración a todos los microservicios)
- **microservice-eureka**: Service Discovery (Eureka Server, registro y descubrimiento de servicios)
- **microservice-gateway**: API Gateway (enrutamiento, seguridad y entrada única)
- **microservice-user**: Microservicio de gestión de usuarios
- **microservice-task**: Microservicio de gestión de tareas
- **microservice-note**: Microservicio de gestión de notas
- **microservice-event**: Microservicio de gestión de eventos

## Requisitos

- Java 17 o superior
- Maven 3.8 o superior
- MySQL (puede estar en la nube o local)

## Configuración centralizada (Config Server)

Toda la configuración sensible y de entorno (puertos, base de datos, JWT, Eureka, rutas, etc.) está centralizada en el Config Server, en archivos YAML por microservicio:

```
microservice-config/src/main/resources/configurations/msvc-user.yml
microservice-config/src/main/resources/configurations/msvc-task.yml
microservice-config/src/main/resources/configurations/msvc-note.yml
microservice-config/src/main/resources/configurations/msvc-event.yml
microservice-config/src/main/resources/configurations/msvc-gateway.yml
microservice-config/src/main/resources/configurations/msvc-eureka.yml
```

Ejemplo de configuración mínima en el archivo local de cada microservicio (`src/main/resources/application-dev.properties`):

```
spring.application.name=msvc-user
spring.config.import=optional:configserver:http://localhost:8888
spring.profiles.active=dev
```

**No dupliques configuración sensible (JWT, DB, etc.) en los archivos locales.**

## Flujo de arranque recomendado

1. **microservice-config** (Config Server) — puerto 8888
2. **microservice-eureka** (Eureka Server) — puerto 8761
3. **microservice-gateway** (API Gateway) — puerto 8080
4. Resto de microservicios: `user` (8080), `task` (8071), `note` (8083), `event` (9091)

## Ejecución

Desde la raíz de cada microservicio:

```
./mvnw spring-boot:run
```

O en Windows:

```
mvnw.cmd spring-boot:run
```

## Arquitectura y comunicación

- Todos los microservicios se registran en Eureka automáticamente.
- El Gateway enruta las peticiones usando Eureka (no usa URLs fijas, sino nombres de servicio: `lb://msvc-user`, etc.).
- La configuración de JWT, base de datos y otros parámetros críticos está solo en el Config Server.
- Si el Config Server no está disponible, los microservicios pueden usar su configuración local (por el `optional:` en `spring.config.import`).

## Buenas prácticas y seguridad

- Mantén las credenciales y secretos solo en el Config Server.
- No subas contraseñas reales ni secretos a repositorios públicos.
- Usa perfiles (`spring.profiles.active`) para separar ambientes (dev, test, prod).
- El Gateway debe validar JWT y enrutar solo a servicios registrados en Eureka.

## Comandos útiles

Compilar todos los microservicios:

```
mvn clean install
```

Ejecutar un microservicio específico:

```
cd microservice-user
./mvnw spring-boot:run
```

## Contacto

- Areliz Isla
- Domingo Andres
- Matias Araos
