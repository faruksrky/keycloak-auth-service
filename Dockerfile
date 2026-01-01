# Multi-stage build for Keycloak Auth Service
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
# Download dependencies (cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/keycloak-auth-service-*.jar app.jar

# Expose port (Railway will override with PORT env var)
EXPOSE 6700

# Run the application
# Railway provides PORT env var automatically
CMD ["java", "-jar", "app.jar"]

