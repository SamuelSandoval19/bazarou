# =====================================================
# BAZAROU - Dockerfile (multi-stage)
# Optimizado para Render free tier
# =====================================================

# --- Stage 1: build con Maven ---
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests -B

# --- Stage 2: imagen final ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar bazarou.jar

# Carpeta para uploads (efimera en Render free)
RUN mkdir -p /tmp/uploads

# Render asigna el puerto via la variable PORT
ENV PORT=8080
EXPOSE 8080

# Activar el perfil de produccion automaticamente
ENV SPRING_PROFILES_ACTIVE=prod

# Limites de memoria optimizados para 512 MB de RAM (Render free)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75", "-jar", "/app/bazarou.jar"]
