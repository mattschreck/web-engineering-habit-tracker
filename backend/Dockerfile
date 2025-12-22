# Stage 1: Build
FROM gradle:8.14-jdk21-alpine AS build

WORKDIR /app

# Copy Gradle files first for caching
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

# Download dependencies (cached layer)
RUN gradle dependencies --no-daemon || return 0

# Copy source code
COPY src ./src

# Build application
RUN gradle bootJar --no-daemon -x test

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Change ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check (using API endpoint)
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/v3/api-docs || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
