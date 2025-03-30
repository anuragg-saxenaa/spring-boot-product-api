# Product Management API

A Spring Boot application that provides a RESTful API for managing products with Kafka integration for event-driven architecture.

## Features

- RESTful API for CRUD operations on products
- H2 in-memory database
- Kafka integration for event-driven architecture
- Swagger UI for API documentation
- Comprehensive test coverage with TestContainers
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

## Running the Application

1. Start Kafka using Docker Compose:
```bash
docker-compose up -d
```

2. Build and run the application:
```bash
mvn clean spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation

Once the application is running, you can access the Swagger UI at:
`http://localhost:8080/swagger-ui.html`

## Running Tests

Run the test suite:
```bash
mvn test
```

The tests use TestContainers to create an isolated Kafka instance for testing.

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