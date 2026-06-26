# EduMind AI API - Project Guide

## Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.5
- **Build:** Maven
- **Database:** PostgreSQL (via JPA/Hibernate)
- **Auth:** JWT (jjwt 0.12.6)
- **Deployment:** AWS Lambda (SAM) + Spring Cloud Function
- **Utils:** Lombok, MapStruct, SpringDoc OpenAPI

## Package Structure
```
com.ai.edumindaiapi
├── common/
│   ├── dto/          # Request/Response DTOs
│   ├── enums/        # Enums (Role, LessonType, etc.)
│   └── exception/    # GlobalExceptionHandler, custom exceptions
├── config/           # OpenAPI, DataInitializer
├── controller/       # REST controllers
├── domain/           # JPA entities
├── jwt/              # JWT filter/service
├── mapper/           # MapStruct mappers
├── repository/       # Spring Data JPA repositories
├── security/         # SecurityConfig, AuthUser, UserDetailsService
└── service/          # Business logic services
```

## Conventions
- Use **constructor injection** (no @Autowired on fields)
- All REST responses wrapped in `ApiResponse<T>`
- Enums implement `BaseEnum` interface
- Use `@Builder` on entities where applicable
- MapStruct for entity ↔ DTO conversion
- Lombok `@Data`, `@Builder`, `@RequiredArgsConstructor`
- Custom exception classes extend RuntimeException

## Common Commands
- `./mvnw clean compile -DskipTests` — Build
- `./mvnw test` — Run tests
- `./mvnw spring-boot:run` — Start dev server (uses .env for DB config)
- `./mvnw clean package -DskipTests` — Package for Lambda deployment
- `sam build && sam deploy` — Deploy to AWS (requires AWS creds)

## API Documentation
Swagger UI available at /swagger-ui.html when running locally.
OpenAPI spec at /v3/api-docs.
