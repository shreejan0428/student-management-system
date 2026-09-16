# Student Management System

A REST API for managing students, courses, enrollments, grades, and administrative users.

## Tech Stack

* Java 21
* Spring Boot
* Spring Web / REST
* Spring Data JPA + Hibernate
* PostgreSQL
* Spring Security
* Docker + Docker Compose
* JUnit 5 + Mockito
* OpenAPI / Swagger UI
* GitHub Actions

## Architecture

```text
Client / Swagger UI
        |
        v
Spring Boot REST API
        |
   Service Layer
        |
  JPA / Hibernate
        |
   PostgreSQL
```

## Features

* Student and course CRUD operations
* Student enrollment and grade management
* Course capacity validation
* Duplicate-enrollment prevention
* Database constraints and input validation
* Transactional enrollment operations
* Role-based API authentication
* Centralized error handling
* Swagger/OpenAPI API documentation
* Automated unit tests
* Dockerized PostgreSQL development environment
* GitHub Actions CI pipeline

## Database Design

The application uses PostgreSQL with four primary tables:

* `students`
* `courses`
* `enrollments`
* `app_users`

Foreign keys maintain relationships between students, courses, and enrollments, while database constraints help prevent invalid and duplicate records.

## Running the Application

### Requirements

* Java 21
* Maven 3.9+
* Docker Desktop

### Start with Docker

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

### Run Tests

```bash
mvn test
```

## Example API Requests

```bash
curl -u admin:admin123 http://localhost:8080/api/students

curl -u admin:admin123 http://localhost:8080/api/courses

curl -u admin:admin123 -X POST http://localhost:8080/api/enrollments \
  -H 'Content-Type: application/json' \
  -d '{"studentId":1,"courseId":1}'

curl -u admin:admin123 http://localhost:8080/api/enrollments/student/1
```

## Authentication

The application uses Spring Security with HTTP Basic authentication and BCrypt password hashing.

Demo accounts are provided for local development:

* `admin`
* `staff`

Production deployments should use securely managed credentials rather than the demo accounts.

## CI/CD

GitHub Actions automatically builds and tests the application to help catch errors before changes are merged.

## Future Deployment

The application is configured to support environment-based database and port configuration, making it suitable for deployment with services such as AWS ECS/Fargate and Amazon RDS for PostgreSQL.
