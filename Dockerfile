#  Etapa de construcción
FROM maven:3.9.4-openjdk-17-slim AS build

# Definimos un argumento para saber qué servicio estamos construyendo
ARG MODULE_NAME

WORKDIR /app

# Copiamos TODO el proyecto (Raíz) para que detecte el pom.xml padre
COPY . .

# [cite: 2] Compilamos SOLO el microservicio específico y sus dependencias (-pl -am)
# Esto es vital para que no tarde años compilando todo el proyecto 5 veces
RUN mvn clean package -DskipTests -pl ${MODULE_NAME} -am

# Etapa final
FROM openjdk:17-jre-slim
ARG MODULE_NAME
WORKDIR /app

# Copiamos el JAR específico desde la carpeta target del módulo
COPY --from=build /app/${MODULE_NAME}/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]