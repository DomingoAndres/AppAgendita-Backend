# Etapa 1: Construir la aplicación con Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY .mvn/ .mvn
COPY pom.xml .
COPY microservice-config/pom.xml ./microservice-config/
COPY microservice-eureka/pom.xml ./microservice-eureka/
COPY microservice-gateway/pom.xml ./microservice-gateway/
COPY microservice-user/pom.xml ./microservice-user/
COPY microservice-task/pom.xml ./microservice-task/
COPY microservice-note/pom.xml ./microservice-note/
COPY microservice-event/pom.xml ./microservice-event/

# Crear las carpetas de todos los módulos (Maven necesita que existan)
RUN mkdir -p microservice-config/src microservice-eureka/src microservice-gateway/src microservice-user/src microservice-task/src microservice-note/src microservice-event/src

# Descargar todas las dependencias (comentado temporalmente por error 502)
# RUN mvn dependency:go-offline -B

# Copiar todas las estructuras de código fuente (Maven necesita validar todos los módulos)
COPY microservice-config/src ./microservice-config/src
COPY microservice-eureka/src ./microservice-eureka/src
COPY microservice-gateway/src ./microservice-gateway/src
COPY microservice-user/src ./microservice-user/src
COPY microservice-task/src ./microservice-task/src
COPY microservice-note/src ./microservice-note/src
COPY microservice-event/src ./microservice-event/src

# Construir solo el módulo necesario
RUN mvn clean package -pl microservice-config -am -DskipTests

# 2. ETAPA DE RUNTIME (Imagen Final)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar el JAR construido desde la etapa anterior
COPY --from=build /app/microservice-config/target/microservice-config*.jar app.jar

# Exponer el puerto (Cloud Run inyectará la variable PORT, por defecto 8080)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
