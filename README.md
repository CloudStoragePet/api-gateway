# api-gateway

API Gateway is a Spring Boot-based microservice that provides a unified API for my microservices for external clients.

## Server port: 8088

## Requirements

- Java version 17.0.7
- Spring Boot 3.1.3
- Maven 3.9.1

## Technologies Used

1. Java 17
2. Spring Boot
3. Spring Cloud Gateway

## Application Features
Routes requests to the appropriate microservice.
- `/api/v1/auth/**`, `/api/v1/validation/**`: user_service

## Running the Application with Docker

To run the Identity Application with Docker, follow these steps:

1. Ensure that Docker and Docker Compose are installed on your system.
2. Clone the repository and navigate to the project directory.
3. Run in `cmd`
   ```sh
      mvn clean install
   ```
4. Build the Docker image by running the following command. Make sure to replace the placeholders `<your_*>` with your
   actual values:

   ```sh
   docker build \
       --build-arg EUREKA_URL=<eureka_url> \
       --build-arg AUTH_VALIDATE_TOKEN_PATH=<endpoint_to_validate_jwt> \
       --build-arg UI_ORIGIN=<frontend_url_for_cors> \
       --build-arg MANAGEMENT_ZIPKIN_TRACING_ENDPOINT=<your_zipkin_endpoint> \
       -t api_gateway .

http://localhost:8080/swagger-ui/index.html