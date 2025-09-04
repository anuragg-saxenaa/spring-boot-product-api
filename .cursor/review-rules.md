# Java Spring Boot Code Review Rules

## Security 🔒
- Check for SQL injection vulnerabilities in queries
- Validate input parameters and sanitize user data
- Ensure proper authentication and authorization checks
- Review sensitive data exposure in logs and responses
- Verify CORS configuration is not overly permissive
- Check for hardcoded secrets or credentials

## Performance ⚡
- Identify N+1 query problems in JPA/Hibernate code
- Look for missing database indices
- Review inefficient loops and collections usage
- Check for proper connection pooling configuration
- Identify memory leaks and resource management issues
- Review caching strategies and implementation

## Spring Framework Best Practices ✨
- Verify proper dependency injection usage (@Autowired, @Component, etc.)
- Check transaction boundaries and @Transactional usage
- Review configuration management (application.properties/yml)
- Ensure proper use of Spring profiles
- Validate component scanning and bean lifecycle
- Check for circular dependencies

## Error Handling ⚠️
- Verify proper exception handling strategies
- Check for meaningful error messages and logging
- Review global exception handlers (@ControllerAdvice)
- Ensure proper HTTP status codes in responses
- Validate error response structure consistency
- Check for proper fallback mechanisms

## Data Validation & Integrity 🛡️
- Verify input validation using Bean Validation (@Valid, @NotNull, etc.)
- Check for proper null safety and Optional usage
- Review data type conversions and potential precision loss
- Validate entity relationships and constraints
- Check for proper validation error handling
- Ensure data consistency across operations

## API Design 🌐
- Review REST endpoint design and naming conventions
- Check HTTP method usage (GET, POST, PUT, DELETE)
- Verify proper status code responses
- Review request/response DTO structure
- Check API versioning strategy
- Validate OpenAPI/Swagger documentation

## Testing 🧪
- Ensure adequate unit test coverage
- Review integration test patterns and setup
- Check for proper mocking strategies
- Verify test data management and cleanup
- Review test naming conventions
- Check for flaky or brittle tests

## Code Quality & Maintainability 📋
- Review adherence to SOLID principles
- Check for code duplication and reusability
- Verify proper naming conventions
- Review method and class size and complexity
- Check for proper separation of concerns
- Validate documentation and comments quality

## Concurrency & Threading 🔄
- Check for thread safety issues
- Review synchronization and locking strategies
- Identify potential race conditions
- Verify proper handling of concurrent requests
- Check for deadlock potential
- Review async processing patterns

## Resource Management 💾
- Verify proper resource cleanup (try-with-resources)
- Check for connection leaks
- Review memory usage patterns
- Validate file handling and I/O operations
- Check for proper stream processing
- Review batch processing efficiency

## Kafka Integration 📡
- Review producer/consumer configuration
- Check message serialization/deserialization
- Verify error handling and retry mechanisms
- Review topic and partition strategies
- Check for proper offset management
- Validate message ordering and deduplication
