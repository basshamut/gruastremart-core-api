# GruastreMart Core API

This is the core API for the GruastreMart application, built with Spring Boot and MongoDB.

## Technology Stack

- Java 21
- Spring Boot 3.3.3
- MongoDB
- JWT Authentication
- Docker
- Maven

## Project Structure

The project follows a standard Spring Boot application structure with Maven as the build tool.

## Authentication

Authentication is implemented using JWT with Spring Security.

(For testing purposes only)

- **Username:** `test@test.com`
- **Password:** `bWlDb250cmFzZcOxYTEyMw==` (The password is `miContraseña123` in Base64)

## Building and Running

### Local Development

To build and run the application locally:

```bash
mvn clean install
mvn spring-boot:run
```

### Docker

Build the Docker image:

```bash
docker build -t gruastremart-core-api .
```

Run the container:

```bash
docker run -p 8080:8080 gruastremart-core-api
```

## Continuous Deployment

The project uses GitHub Actions for CI/CD. The workflow defined in `deploy.yml` handles:

1. Building the application
2. Creating a Docker image
3. Saving the Docker image as a tarball
4. Copying the tarball to a VPS
5. Deploying the container on the VPS

## Dependencies

Major dependencies include:

- Spring Boot: `3.3.3`
- SpringDoc OpenAPI: `2.3.0`
- Lombok: `1.18.30`
- MapStruct: `1.5.3.Final`
- JWT: `0.11.5`
- Caffeine Cache: `3.1.8`
- Liquibase (for MongoDB): `4.31.1`

### Testing dependencies:

- JUnit Jupiter: `5.9.3`
- Cucumber: `7.18.0`
- Embedded MongoDB: `4.6.1`

## API Documentation

The API is documented using SpringDoc OpenAPI.

When the application is running, the Swagger UI is available at:

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
