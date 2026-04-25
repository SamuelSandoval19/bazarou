# =====================================================
# BAZAROU - Dockerfile (multi-stage build)
# =====================================================

# --- Stage 1: build con Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
# Copia primero el pom.xml para aprovechar la cache de capas
COPY pom.xml .
RUN mvn dependency:go-offline -B
# Luego el código fuente y compila
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: imagen final, solo el JAR ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar bazarou.jar

# Carpeta para subir imágenes (volumen)
RUN mkdir -p /app/uploads
VOLUME /app/uploads

EXPOSE 8080
ENV BAZAROU_UPLOADS=/app/uploads

ENTRYPOINT ["java", "-jar", "/app/bazarou.jar"]
