# Product Management API

A Spring Boot application that provides a RESTful API for managing products with Kafka integration for event-driven architecture.

## Features

- RESTful API for CRUD operations on products
- H2 in-memory database
- Kafka integration for event-driven architecture
- Swagger UI for API documentation
- Comprehensive test coverage
- Separate configurations for development and test environments

## Prerequisites

- Java 17 or higher
- Maven
- Docker (for running Kafka locally)

## Project Structure

```
src/
├── main/
│   └── java/
│       └── com/
│           └── arrayindex/
│               └── productmanagementapi/
│                   ├── controller/
│                   ├── model/
│                   ├── repository/
│                   ├── service/
│                   └── ProductManagementApplication.java
└── test/
    └── java/
        └── com/
            └── arrayindex/
                └── productmanagementapi/
                    ├── controller/
                    └── service/
```

## Configuration

The application uses different configurations for development and test environments:

- Development: `src/main/resources/application.properties`
- Test: `src/test/resources/application-test.properties`

## Running the Application Locally

### Step 1: Start Kafka

1. Make sure Docker is running on your machine
2. Navigate to the project root directory
3. Start Kafka using Docker Compose:
```bash
docker-compose up -d
```
4. Verify Kafka is running:
```bash
docker ps
```
You should see Kafka and Zookeeper containers running.

### Step 2: Build and Run the Application

1. Clean and build the project:
```bash
mvn clean install
```

2. Run the application:
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Step 3: Verify the Application

1. Access the H2 Console:
   - URL: `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:productdb`
   - Username: `sa`
   - Password: `password123`

2. Access Swagger UI:
   - URL: `http://localhost:8080/swagger-ui.html`

3. Test the API endpoints using Swagger UI or any API client (like Postman)

## Running Tests

### Prerequisites for Tests

No external dependencies required. Tests use embedded Kafka.

### Running All Tests

```bash
mvn clean test
```

This will run:
- Unit tests
- Integration tests
- Kafka integration tests

### Running Specific Test Classes

1. Run only controller tests:
```bash
mvn test -Dtest=ProductControllerIntegrationTest
```

2. Run only service tests:
```bash
mvn test -Dtest=ProductServiceTest
```

3. Run only Kafka integration tests:
```bash
mvn test -Dtest=KafkaIntegrationTest
```

### Test Coverage Report

To generate a test coverage report:
```bash
mvn clean test jacoco:report
```
The report will be available at: `target/site/jacoco/index.html`

### Test Environment Details

The test environment uses:
- H2 in-memory database
- Local Kafka instance
- Disabled Swagger UI
- Separate test configuration file

## Troubleshooting

### Common Issues

1. Kafka Connection Issues
   - Ensure Docker is running
   - Check if Kafka containers are up: `docker ps`
   - Verify Kafka port (9092) is not in use
   - Check application logs for connection errors

2. Test Failures
   - Ensure Docker has enough resources allocated
   - Check if TestContainers can pull images
   - Verify test configuration in `application-test.properties`

3. H2 Database Issues
   - Verify database URL in application.properties
   - Check if H2 console is accessible
   - Ensure no other application is using the same port

## API Endpoints

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get a product by ID
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update an existing product
- `DELETE /api/products/{id}` - Delete a product

## Development

### Project Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd product-management-api
```

2. Build the project:
```bash
mvn clean install
```

### Development Environment

The development environment uses:
- H2 in-memory database
- Local Kafka instance
- Swagger UI for API documentation

### Test Environment

The test environment uses:
- H2 in-memory database
- TestContainers for Kafka
- Disabled Swagger UI

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.