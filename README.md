# Product Management API

A robust Spring Boot application that provides a RESTful API for managing products. This API includes comprehensive CRUD operations, built with modern Spring Boot practices and best practices for enterprise applications.

## Features

- CRUD operations for Product entity
- H2 in-memory database
- OpenAPI/Swagger documentation
- Comprehensive test coverage (Unit & Integration tests)
- RESTful API endpoints
- Secure Git credential management
- Kafka integration for asynchronous processing
- Proper HTTP status codes for all operations
- Global exception handling

## Technologies

- Java 21
- Spring Boot 3.2.5
- Spring Data JPA
- H2 Database
- Lombok
- SpringDoc OpenAPI UI
- Maven
- Apache Kafka
- TestContainers

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Git (with credential manager configured)
- Docker (for running Kafka in tests)

### Git Setup

This project uses Git credential manager for secure authentication. To set it up:

1. Configure Git to use the macOS keychain:
   ```bash
   git config --global credential.helper osxkeychain
   ```

2. The first time you push to GitHub, you'll be prompted for your credentials:
   - Username: your GitHub username
   - Password: your GitHub Personal Access Token (PAT)

3. Your credentials will be securely stored in the macOS keychain for future use.

### Running the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/anuragg-saxenaa/product-management-api.git
   cd product-management-api
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### API Documentation

Once the application is running, you can access the API documentation at:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## API Endpoints

### Products API

- GET `/api/products` - Get all products (200 OK)
- GET `/api/products/{id}` - Get a product by ID (200 OK, 404 Not Found)
- POST `/api/products` - Create a new product (201 Created)
- PUT `/api/products/{id}` - Update a product (200 OK, 404 Not Found)
- DELETE `/api/products/{id}` - Delete a product (200 OK, 404 Not Found)

## Database

The application uses H2 in-memory database. You can access the H2 console at:
`http://localhost:8080/h2-console`

Database Configuration:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Kafka Integration

The application uses Kafka for asynchronous processing of product operations. Kafka configuration is handled through Spring profiles:

- Development: Uses the default Kafka configuration
- Test: Uses TestContainers for running Kafka in tests

### Kafka Topics
- `products`: Topic for product-related events

## Testing

The project includes comprehensive test coverage with:
- Unit tests for service layer
- Integration tests for controller layer
- Kafka integration tests using TestContainers

To run the tests:

```bash
mvn test
```

## Error Handling

The application includes a global exception handler that provides consistent error responses:
- 404 Not Found: When a product is not found
- 400 Bad Request: For invalid input
- 500 Internal Server Error: For unexpected errors

## Recent Changes

- Added Kafka integration for asynchronous processing
- Implemented proper HTTP status codes for all operations
- Added global exception handling
- Enhanced test coverage with Kafka integration tests
- Improved error handling with custom exceptions

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 