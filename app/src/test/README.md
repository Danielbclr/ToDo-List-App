# Testing with JUnit and JaCoCo

This project uses JUnit for unit testing and JaCoCo for code coverage analysis.

## Running Tests

You can run unit tests with the following command:

```bash
./gradlew test
```

To run tests with coverage enabled:

```bash
./gradlew testDebugUnitTest
```

## Generating Coverage Reports

To generate a JaCoCo coverage report:

```bash
./gradlew jacocoTestReport
```

This will generate reports in HTML and XML formats that can be found at:
- HTML: `app/build/reports/jacoco/jacocoTestReport/html/index.html`
- XML: `app/build/reports/jacoco/jacocoTestReport/jacocoTestReport.xml`

## Test Structure

The project follows a standard testing structure:
- `app/src/test/java/` - Unit tests
- `app/src/androidTest/java/` - Instrumented tests

### Key Test Classes

- **ViewModel Tests**: Tests for ViewModels using MockK to mock dependencies
- **Repository Tests**: Tests for repositories mocking the API services

## Test Dependencies

Key testing dependencies include:
- JUnit: Core testing framework
- MockK: Mocking library for Kotlin
- Mockito: Additional mocking capabilities
- kotlinx-coroutines-test: For testing coroutines

## Writing New Tests

When writing new tests:

1. Follow the AAA pattern (Arrange, Act, Assert)
2. Mock external dependencies
3. Use `runTest` for testing coroutines
4. For ViewModels, use test dispatchers to control coroutine execution

## Test Coverage Goals

- Aim for at least 80% code coverage for business logic
- Focus on testing:
  - ViewModels
  - Repositories
  - Use cases and business logic
  - Edge cases and error handling 