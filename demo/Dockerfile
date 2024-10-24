# Build-Stage: Verwende Maven, um das Projekt zu bauen
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Final-Stage: Verwende die Java-Laufzeitumgebung, um die Applikation zu starten
FROM eclipse-temurin:17-jdk
WORKDIR /app
# Kopiere das erzeugte JAR aus dem Build-Stage
COPY --from=build /app/target/cloud-consumption-app-1.0-SNAPSHOT.jar /app/your-app.jar

EXPOSE 8080
# Starte die Applikation mit Java
CMD ["java", "-jar", "your-app.jar"]
