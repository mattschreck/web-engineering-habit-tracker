# Habit Tracker Frontend

A SvelteKit application built with Svelte 5 and Bun.

## Core Technologies

- Framework: SvelteKit (using Svelte 5 Runes)
- Package Manager: Bun
- Styling: Tailwind CSS v4
- Components: Shadcn-Svelte

## Local Development

### Prerequisites

- Bun installed on the host system.

### Commands

```bash
# Install dependencies
bun install

# Start development server
bun run dev

# Format code
bun run format
```

## Integration

- API Client: The application communicates with the Spring Boot backend via a centralized client located in src/lib/api/client.ts.
- Environment: Uses dynamic environment variables for the API URL to support different deployment environments.
