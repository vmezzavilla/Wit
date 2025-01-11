
# Wit Calculator Challenge

## Description
This project is a distributed calculator application using a microservices architecture, Spring Boot, and Kafka. It handles numerical operations (addition, subtraction, multiplication, and division) by leveraging Kafka messaging for communication between services. The application consists of two major components:

1. **Calculator Service**: Performs the numerical operations.
2. **REST Service**: Provides a RESTful API for users to interact with the system via HTTP endpoints.

## Features
- Distributed microservices-based architecture.
- Asynchronous processing using Kafka for requests and responses.
- Four mathematical operations (add, subtract, multiply, divide).
- Dockerized environment for deployment.

## Architecture Overview
1. **REST Service**:
   - Exposes endpoints to trigger operations.
   - Sends the requests to Kafka topics (operation-specific) and waits asynchronously for responses.
   - Endpoints include:
     - `/sum`: Adds two numbers.
     - `/subtraction`: Subtracts the second number from the first number.
     - `/multiplication`: Multiplies two numbers.
     - `/division`: Divides the first number by the second number.

2. **Calculator Service**:
   - Listens to Kafka topics for incoming operation requests.
   - Performs the requested numeric computation.
   - Publishes the computed result back to a Kafka response topic.

## Prerequisites
- Java 21 installed.
- Docker and Docker Compose installed.
- Kafka (configured via Docker Compose).

## How to Run
Follow the steps below to run the application:

1. **Clone the Repository**:
   ```bash
   git clone <repository-url>
   cd wit-calculator
   ```

2. **Build the Project**:
   ```bash
   mvn clean package -DskipTests
   ```

3. **Run with Docker Compose**:
   ```bash
   docker-compose up --build
   ```

4. The REST service will be available at `http://localhost:8080`.

## API Endpoints
- **Addition**:
  ```http
  GET /sum?a=<number>&b=<number>
  ```
  Example: `GET http://localhost:8080/sum?a=5&b=3` (Result: 8).

- **Subtraction**:
  ```http
  GET /subtraction?a=<number>&b=<number>
  ```
  Example: `GET http://localhost:8080/subtraction?a=5&b=3` (Result: 2).

- **Multiplication**:
  ```http
  GET /multiplication?a=<number>&b=<number>
  ```
  Example: `GET http://localhost:8080/multiplication?a=5&b=3` (Result: 15).

- **Division**:
  ```http
  GET /division?a=<number>&b=<number>
  ```
  Example: `GET http://localhost:8080/division?a=6&b=3` (Result: 2).

## Configuration
Application configurations are defined in `application.properties` or can be overridden via environment variables:
- Kafka configuration:
  ```properties
  spring.kafka.bootstrap-servers=kafka:9092
  spring.kafka.consumer.group-id=calculator-group

  kafka.sum-topic=sum-topic
  kafka.sub-topic=sub-topic
  kafka.mult-topic=mult-topic
  kafka.div-topic=div-topic
  kafka.response-topic=response-topic
  ```

## Logging
Logs are configured in `logback-spring.xml` with a custom pattern using request IDs. Logs provide insight into requests and processing stages for troubleshooting.

## Testing
Run unit tests for the Calculator service using:
```bash
mvn test
```

## Docker
Docker builds are configured for both REST and Calculator services using the respective Dockerfiles in their directories. See the `docker-compose.yml` for service orchestration.

## License
This project is licensed under the MIT License.
