# Etapa 1: Compilación de la aplicación con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el descriptor del proyecto y las fuentes
COPY pom.xml .
COPY src ./src

# Compilamos el proyecto omitiendo los tests
RUN mvn clean package -DskipTests

# Etapa 2: Imagen de ejecución con Java 21
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el archivo .jar generado
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto del servidor
EXPOSE 8081

# Comando de inicio
ENTRYPOINT ["java", "-jar", "app.jar"]