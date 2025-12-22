# Docker Configuration

This project uses Docker Compose to manage the development and production environments.

## Services

- Frontend: Accessible on port 5173
- Backend: Accessible on port 8080
- PostgreSQL: Accessible on port 5432

## Basic Commands

Start all services:
```bash
docker compose up --build
```

Stop and remove containers:
```bash
docker compose down
```

## Maintenance

To completely reset the database and application state:
```bash
docker compose down -v
docker compose up --build
```

The frontend uses the oven/bun image to ensure fast builds and consistent package management. The backend uses the eclipse-temurin JRE image for the runtime.