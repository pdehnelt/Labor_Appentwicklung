# Use a Maven image to build the project
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Use a Java image to run the application
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/your-app.jar /app/your-app.jar
EXPOSE 8080
CMD ["java", "-jar", "your-app.jar"]
