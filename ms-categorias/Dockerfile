# ============================================================
# Multi-stage Dockerfile para ms-categorias
# ============================================================

# ---- Etapa 1: build ----
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /build

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---- Etapa 2: runtime ----
FROM eclipse-temurin:17-jre-jammy

# Instalar wget para healthchecks HTTP
RUN apt-get update \
    && apt-get install -y --no-install-recommends wget ca-certificates \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=builder /build/target/ms-categorias-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8084

ENTRYPOINT ["java", "-jar", "app.jar"]
