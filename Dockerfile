# Etapa de construcción (Usamos Eclipse Temurin con Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .

# --- CAMBIO CLAVE AQUÍ ---
# En vez de compilar todo, usamos "-pl" (Project List) para compilar SOLO lo que usamos.
# Esto evita que Maven intente arreglar el desastre de Eureka y falle.
RUN mvn clean package -DskipTests -pl microservice-gateway,microservice-user,microservice-task,microservice-note,microservice-event -am

# Etapa final (Usamos Eclipse Temurin JRE ligero)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos los JARs (Eureka y Config ya no se generan, y no los copiamos)
COPY --from=build /app/microservice-gateway/target/*.jar /app/msvc-gateway.jar
COPY --from=build /app/microservice-user/target/*.jar /app/msvc-user.jar
COPY --from=build /app/microservice-task/target/*.jar /app/msvc-task.jar
COPY --from=build /app/microservice-note/target/*.jar /app/msvc-note.jar
COPY --from=build /app/microservice-event/target/*.jar /app/msvc-event.jar

EXPOSE 8080

# Comando de arranque dinámico
ENTRYPOINT ["sh", "-c", "java -jar /app/${JAR_NAME}.jar"]