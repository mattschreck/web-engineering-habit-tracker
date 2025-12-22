# Habit Tracker Backend

A Kotlin-based Spring Boot application providing the REST API for the Habit Tracker project.

## Features

- API Documentation: Integrated Scalar UI available at /scalar/api-docs.
- Authentication: Stateless JWT (JSON Web Token) implementation.
- Database: PostgreSQL with Flyway for schema migrations.
- Architecture: Controller-Service-Repository pattern.

## Setup and Development

The project is configured to run via Docker Compose, but can be started locally for development.

### Prerequisites
- Java 21
- PostgreSQL (if not using Docker)

### Commands
```bash
# Run application locally
./gradlew bootRun

# Execute tests
./gradlew test

# Build executable JAR
./gradlew bootJar -x test
```

## Database Management
- Migrations: SQL files are located in src/main/resources/db/migration.
- Configuration: Database connection details are managed via environment variables in the docker-compose.yml file.