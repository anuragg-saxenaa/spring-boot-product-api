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
./mvnw clean install
```

2. Run the application:
```bash
./mvnw spring-boot:run
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
./mvnw test
```

This will run:
- Unit tests
- Integration tests
- Kafka integration tests

### Running Specific Test Classes

1. Run only controller tests:
```bash
./mvnw -Dtest=ProductControllerIntegrationTest
```

2. Run only service tests:
```bash
./mvnw -Dtest=ProductServiceTest
```

3. Run only Kafka integration tests:
```bash
./mvnw -Dtest=KafkaIntegrationTest
```

### Test Coverage Report

To generate a test coverage report:
```bash
./mvnw test jacoco:report
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
./mvnw clean install
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

## Automated PR Reviews (Cursor CLI)

This repository includes automated code review using Cursor CLI that runs on every pull request. The system analyzes code changes and provides intelligent feedback focusing on Java/Spring Boot best practices.

### What the Action Does

- **Automated Analysis**: Reviews PR diffs using AI-powered analysis
- **Spring Boot Focus**: Specialized rules for Java, Spring Boot, JPA/Hibernate, and Kafka
- **Inline Comments**: Posts specific feedback directly on changed lines
- **Smart Detection**: Identifies security issues, performance problems, and code quality concerns
- **Non-Blocking**: Provides advisory feedback without blocking PR merges

### Setup Requirements

To enable automated reviews, add the following secret to your repository:

1. Go to Settings → Secrets and variables → Actions
2. Add a new secret named `CURSOR_API_KEY`
3. Set the value to your Cursor API key (obtain from your Cursor account)

### Review Focus Areas

The automated reviewer checks for:

- 🔒 **Security**: SQL injection, authentication issues, data exposure
- ⚡ **Performance**: N+1 queries, inefficient loops, resource management
- ✨ **Spring Best Practices**: Dependency injection, transactions, configuration
- ⚠️ **Error Handling**: Exception management, meaningful error messages
- 🛡️ **Data Validation**: Input validation, null safety, constraints
- 🌐 **API Design**: REST conventions, HTTP status codes, response structure
- 🧪 **Testing**: Coverage, patterns, maintainability
- 📋 **Code Quality**: SOLID principles, clean code practices

### Running Locally

To run code review locally using Cursor CLI:

```bash
# Install Cursor CLI
curl https://cursor.com/install -fsS | bash

# Set your API key
export CURSOR_API_KEY="your-api-key-here"

# Review current changes
cursor-agent --model "gpt-4" --print "Review the current git diff for Java/Spring Boot best practices"
```

### Customizing Review Rules

Review rules can be customized by editing `.cursor/review-rules.md`. The current configuration focuses on:

- Java 17 and Spring Boot patterns
- Maven project structure
- JPA/Hibernate best practices
- Kafka integration patterns
- Docker deployment considerations

### Workflow Configuration

The review workflow (`.github/workflows/cursor-code-review.yml`) triggers on:
- Pull request opened
- Pull request synchronized (new commits)
- Pull request reopened
- Pull request marked ready for review

Reviews are limited to a maximum of 10 inline comments per PR, prioritizing the most critical issues.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request (automated review will run automatically)

## License

This project is licensed under the MIT License - see the LICENSE file for details.