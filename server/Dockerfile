# Stage 1: Build the JAR
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml first (so Maven dependencies are cached in a separate layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run the JAR
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/minicache-server-1.0-SNAPSHOT.jar app.jar

# TCP port for clients
EXPOSE 6379
# HTTP port for dashboard
EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]