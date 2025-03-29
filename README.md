# Spring Boot Product API

This is a Spring Boot application that provides a RESTful API for managing products. It includes CRUD operations and is built with modern Spring Boot practices.

## Features

- CRUD operations for Product entity
- H2 in-memory database
- OpenAPI/Swagger documentation
- Comprehensive test coverage (Unit & Integration tests)
- RESTful API endpoints
- Secure Git credential management

## Technologies

- Java 21
- Spring Boot 3.2.5
- Spring Data JPA
- H2 Database
- Lombok
- SpringDoc OpenAPI UI
- Maven

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Git (with credential manager configured)

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
   git clone [repository-url]
   cd demo1
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

- GET `/api/products` - Get all products
- GET `/api/products/{id}` - Get a product by ID
- POST `/api/products` - Create a new product
- PUT `/api/products/{id}` - Update a product
- DELETE `/api/products/{id}` - Delete a product

## Database

The application uses H2 in-memory database. You can access the H2 console at:
`http://localhost:8080/h2-console`

Database Configuration:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Testing

The project includes both unit and integration tests. To run the tests:

```bash
mvn test
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 