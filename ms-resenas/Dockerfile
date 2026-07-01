FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/ms-resenas-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8091

ENTRYPOINT ["java", "-jar", "app.jar"]
