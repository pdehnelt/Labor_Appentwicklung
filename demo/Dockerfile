# Stage 1: Build the application using Maven and OpenJDK 17
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
# Stage 2: Run the application
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/cloud-consumption-app-1.0-SNAPSHOT.jar /app/cloud-consumption-app-1.0-SNAPSHOT.jar
EXPOSE 8080
CMD ["java", "-jar", "/app/cloud-consumption-app-1.0-SNAPSHOT.jar"]