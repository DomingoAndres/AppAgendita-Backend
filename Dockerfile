# Etapa de construcción (Usamos Eclipse Temurin con Maven)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# Compilamos TODOS los módulos ignorando tests
RUN mvn clean package -DskipTests

# Etapa final (Usamos Eclipse Temurin JRE ligero)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiamos los JARs
COPY --from=build /app/microservice-gateway/target/*.jar /app/msvc-gateway.jar
COPY --from=build /app/microservice-user/target/*.jar /app/msvc-user.jar
COPY --from=build /app/microservice-task/target/*.jar /app/msvc-task.jar
COPY --from=build /app/microservice-note/target/*.jar /app/msvc-note.jar
COPY --from=build /app/microservice-event/target/*.jar /app/msvc-event.jar

EXPOSE 8080

# Comando de arranque dinámico
ENTRYPOINT ["sh", "-c", "java -jar /app/${JAR_NAME}.jar"]