FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY target/ms-sucursales-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", "-jar", "app.jar"]
