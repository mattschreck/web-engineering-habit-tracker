# Habit Tracker (Web Engineering)

A full-stack habit tracking application built as a university semester project.

## Quick Start

1. Prerequisites: Docker and Docker Compose.
2. Start Application:
   ```bash
   docker compose up --build
   ```
3. Access:
   - Frontend: http://localhost:5173
   - Backend API: http://localhost:8080
   - API Documentation (Scalar): http://localhost:8080/scalar/api-docs

## Project Structure

- /backend: Kotlin Spring Boot application.
  - REST API, JWT Authentication, Database Migrations (Flyway).
- /frontend: SvelteKit application.
  - Developed with Bun, Tailwind CSS v4, and Shadcn UI.
- docker-compose.yml: Orchestrates Backend, Frontend, and PostgreSQL services.

## Tech Stack

- Frontend: SvelteKit (Svelte 5), Bun, Tailwind v4
- Backend: Kotlin, Spring Boot 3, Gradle
- Database: PostgreSQL 16 (Managed via Flyway)
- API Docs: Scalar (OpenAPI/Swagger)
