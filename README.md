# Student Management System

![CI](https://github.com/YOUR_GITHUB_USERNAME/student-management-system/actions/workflows/ci.yml/badge.svg)

A production-style REST API for managing students, courses, enrollments, grades, and administrative reports.

## Tech stack

- Java 21
- Spring Boot
- Spring Web / REST
- Spring Data JPA + Hibernate
- PostgreSQL
- Spring Security (HTTP Basic + BCrypt)
- Docker + Docker Compose
- JUnit 5 + Mockito
- OpenAPI / Swagger UI
- GitHub Actions CI
- AWS-ready configuration through environment variables

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

- CRUD operations for students and courses
- Enrollment and grade management
- Database uniqueness constraints and validation
- Capacity checks for courses
- Duplicate-enrollment protection
- Transactional enrollment operations
- Role-aware authenticated API users
- Centralized API error responses
- Swagger/OpenAPI documentation
- Automated unit tests
- Dockerized local development
- CI pipeline with GitHub Actions

## Run locally

Requirements: Java 21, Maven 3.9+, Docker Desktop.

```bash
mvn test
mvn spring-boot:run
```

Or run the complete stack (app + PostgreSQL) with one command:

```bash
docker compose up --build
```

API: http://localhost:8080
Swagger UI: http://localhost:8080/swagger-ui.html

### Setup in VS Code (macOS)

1. Install prerequisites: `brew install openjdk@21 maven` and Docker Desktop.
2. Open the project folder in VS Code and install the **Extension Pack for Java** and **Spring Boot Extension Pack** when prompted.
3. Start PostgreSQL only, so you can run/debug the app from VS Code: `docker compose up postgres`.
4. Open `StudentManagementApplication.java` and click **Run** (or **Debug**) above the `main` method, or press F5.
5. Visit http://localhost:8080/swagger-ui.html to try the API.

Demo credentials:

- `admin` / `admin123`
- `staff` / `staff123`

For a real deployment, change these seeded credentials and use environment/secret management instead of committing passwords.

## Example API calls

```bash
curl -u admin:admin123 http://localhost:8080/api/students

curl -u admin:admin123 http://localhost:8080/api/courses

curl -u admin:admin123 -X POST http://localhost:8080/api/enrollments \\
  -H 'Content-Type: application/json' \\
  -d '{"studentId":1,"courseId":1}'

curl -u admin:admin123 http://localhost:8080/api/enrollments/student/1
```

## Database design

`students`, `courses`, `enrollments`, and `app_users` are connected with foreign keys and uniqueness constraints. The enrollment table prevents a student from being enrolled in the same course twice.

## AWS deployment

The application reads database and port settings from environment variables:

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `PORT`

A straightforward AWS deployment is to run the Docker image on ECS/Fargate and use Amazon RDS for PostgreSQL. Do not put production passwords in source control.

