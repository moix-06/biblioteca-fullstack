# ============================================================
# Multi-stage Dockerfile para eureka-server
# Etapa 1: compila con Maven (no requiere Maven en el host)
# Etapa 2: imagen final liviana con solo JRE
# ============================================================

# ---- Etapa 1: build ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

# Copiar pom.xml y descargar dependencias (aprovecha cache de capas)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copiar el codigo y compilar
COPY src ./src
RUN mvn -B -q -DskipTests package

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:17-jre-jammy

# Instalar wget para healthchecks HTTP
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /build/target/eureka-server-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]
