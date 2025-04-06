# Jabberpoint Repair Project

Jabberpoint is a slide show presentation tool originally developed at the Open University as a teaching tool for software design and quality. This project aims to repair and improve the Jabberpoint presentation software as part of a software quality course assignment, with a focus on implementing proper CI/CD and quality assurance practices.

## About Jabberpoint

Jabberpoint is a simple Java-based presentation application that allows users to:
- Create slide presentations with text elements
- Navigate between slides
- Load and save presentations in a custom format

The application serves as an excellent case study for applying software design principles, refactoring techniques, and implementing quality assurance processes.

## Documentation

Detailed documentation can be found in the `docs` folder:
- [DTAP Workflow](docs/DTAP_Workflow.md) - Explains our DTAP street implementation
- [JaCoCo Setup](docs/JaCoCo_Setup.md) - Details on our code coverage configuration
- [Contributing Guidelines](docs/Contributing.md) - How to contribute to this project
- [CI/CD Pipeline](docs/CI_CD_Pipeline.md) - Overview of our continuous integration process

## DTAP Street

This project uses a DTAP (Development, Testing, Acceptance, Production) workflow:

- **Development**: Where active development happens
- **Testing**: Where code is thoroughly tested
- **Acceptance**: Where stakeholders can review changes
- **Production**: The stable, release-ready version

## Workflow

1. Create feature branches from `development`
2. Submit Pull Requests to merge into `development`
3. When ready, merge `development` into `testing`
4. After testing, merge into `acceptance`
5. Finally, release by merging into `main` (production)

## Code Quality

This project uses:
- JaCoCo for code coverage reporting (minimum 70% instruction coverage, 60% branch coverage)
- GitHub Actions for CI/CD pipeline implementation
- Google Java Format for consistent code style
- Maven Enforcer Plugin for dependency management
- Automated integration testing across the DTAP pipeline

Code quality is enforced at multiple levels:
1. Pre-commit with local build validation
2. During pull request reviews
3. During automated testing on all DTAP environments

### Accessing Code Coverage Reports

JaCoCo code coverage reports are automatically generated during CI/CD pipeline runs and are available as downloadable artifacts:

1. Go to the [Actions tab](https://github.com/StefanIonutTasca/Software_Quality_2025/actions) in the GitHub repository
2. Select the "JaCoCo Code Coverage - Java CI/CD with Maven" workflow
3. Choose the latest successful run
4. Scroll down to the "Artifacts" section
5. Download the "jacoco-report" artifact
6. Extract the ZIP file and open `index.html` in your browser to view the detailed coverage report

For more details on our code coverage setup and configuration, see the [JaCoCo Setup](docs/JaCoCo_Setup.md) documentation.

## Getting Started

### Prerequisites
- Java 17 or higher
- Git

Maven is **not necessary** for simply running the project. It's only required if you want to:
- Build the project from source
- Run the automated tests
- Generate code coverage reports

### Running the Application
There are two ways to run the application:

#### Option 1: Run the Main File Directly (No Maven Required)
Simply run the main JabberPoint class located at:
```
src/main/java/org/jabberpoint/src/app/JabberPoint.java
```

#### Option 2: Building from Source (Maven Required)
1. Clone the repository
```bash
git clone https://github.com/YourUsername/Software_Quality_2025.git
cd Software_Quality_2025
```

2. Build the project
```bash
mvn clean install
```

3. Run the application
```bash
mvn exec:java -Dexec.mainClass="org.jabberpoint.JabberPoint"
```

### Automated Testing
Our project includes both unit tests and integration tests:

#### Unit Tests
Run unit tests with:
```bash
mvn test
```

#### Integration Tests
Integration tests are located at:
```
src/test/java/org/jabberpoint/integration/
```

These tests verify the interaction between different components of the application and run automatically on pull requests to testing, acceptance, and main branches.

#### Accessing Test Reports
Test reports and code coverage reports are automatically generated during CI/CD pipeline runs:

1. Go to the [Actions tab](https://github.com/StefanIonutTasca/Software_Quality_2025/actions) in the GitHub repository
2. Select the relevant workflow:
   - "JaCoCo Code Coverage - Java CI/CD with Maven" for code coverage reports
   - "Integration Tests" for integration test results
3. Choose the latest successful run
4. Scroll down to the "Artifacts" section
5. Download the relevant artifact ("jacoco-report" for code coverage)
6. Extract the ZIP file and open `index.html` in your browser to view the detailed report

### Development Workflow
1. Create a feature branch from `development`
2. Make your changes
3. Submit a pull request following the guidelines in [Contributing](docs/Contributing.md)
4. Ensure all CI checks pass
5. Get your PR reviewed and merged
