# Stage 1: Build Java application
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom.xml and fetch dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build package
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Minimal Runtime Image
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy built JAR from builder
COPY --from=builder /app/target/json-parser-1.0.0-SNAPSHOT.jar app.jar

# Expose port (default 8080 for Cloud services like Render / Railway)
ENV PORT=8080
EXPOSE 8080

# Run HTTP Server
ENTRYPOINT ["java", "-jar", "app.jar"]
