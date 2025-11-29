# 1. ETAPA DE BUILD (Builder)
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copiar todos los POMs para descargar dependencias y aprovechar el caché
COPY pom.xml ./
COPY microservice-config/pom.xml ./microservice-config/
COPY microservice-eureka/pom.xml ./microservice-eureka/
COPY microservice-gateway/pom.xml ./microservice-gateway/
COPY microservice-event/pom.xml ./microservice-event/
COPY microservice-note/pom.xml ./microservice-note/
COPY microservice-task/pom.xml ./microservice-task/
COPY microservice-user/pom.xml ./microservice-user/

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
RUN mvn clean package -pl microservice-eureka -am -DskipTests

# 2. ETAPA DE RUNTIME (Imagen Final)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia el JAR compilado desde la etapa de build
COPY --from=builder /app/microservice-eureka/target/microservice-eureka*.jar app.jar

# Puerto interno del contenedor
EXPOSE 8761

# Comando de ejecución
ENTRYPOINT ["java","-jar","/app/app.jar"]