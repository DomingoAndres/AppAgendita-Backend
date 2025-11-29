# Etapa de construcción
FROM maven:3.9.4-openjdk-17-slim AS build
WORKDIR /app
COPY . .
# Compilamos TODOS los módulos de una vez desde la raíz
RUN mvn clean package -DskipTests

# Etapa final
FROM openjdk:17-jre-slim
WORKDIR /app

# Copiamos TODOS los JARs y les ponemos nombres fijos y fáciles
COPY --from=build /app/microservice-gateway/target/*.jar /app/msvc-gateway.jar
COPY --from=build /app/microservice-user/target/*.jar /app/msvc-user.jar
COPY --from=build /app/microservice-task/target/*.jar /app/msvc-task.jar
COPY --from=build /app/microservice-note/target/*.jar /app/msvc-note.jar
COPY --from=build /app/microservice-event/target/*.jar /app/msvc-event.jar

EXPOSE 8080

# Usamos la variable de entorno JAR_NAME para decidir cuál arrancar
ENTRYPOINT ["sh", "-c", "java -jar /app/${JAR_NAME}.jar"]