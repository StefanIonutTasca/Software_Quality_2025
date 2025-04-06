# DTAP Workflow for Jabberpoint Project

This document outlines the Development, Testing, Acceptance, and Production (DTAP) workflow for the Jabberpoint repair project.

## Branch Structure

The project uses the following branch structure:

- **development**: This is where all active development happens. Feature branches should be created from and merged back to this branch.
- **testing**: Once features are complete in development, they are merged to testing for thorough testing.
- **acceptance**: After passing tests, changes move to acceptance for stakeholder review.
- **main (production)**: The stable, production-ready code.

## Workflow Steps

1. Create a feature branch from `development`:
   ```
   git checkout -b feature/your-feature-name development
   ```

2. Implement your feature with frequent commits:
   ```
   git add .
   git commit -m "Descriptive commit message"
   ```

3. When finished, create a Pull Request to merge back to `development`

4. After code review and approval, merge the PR

5. When ready to move to testing:
   ```
   git checkout testing
   git merge development
   git push origin testing
   ```

6. After passing tests, move to acceptance:
   ```
   git checkout acceptance
   git merge testing
   git push origin acceptance
   ```

7. Finally, for production release:
   ```
   git checkout main
   git merge acceptance
   git push origin main
   ```

## Code Quality Checks

All code changes go through the following quality gates:

1. Unit tests must pass
2. Code coverage must meet minimum thresholds (set in the JaCoCo configuration)
3. Code review approval required before merging
4. Static code analysis must pass (to be implemented)
5. Integration tests on the testing branch

## CI/CD Pipeline

Our GitHub Actions workflow automatically runs:

1. Code compilation
2. Unit tests
3. JaCoCo code coverage reports
4. Integration tests on pull requests to testing, acceptance, and main branches

### Workflow Configuration

Our workflow files are configured to optimize the CI/CD pipeline and prevent duplicate runs:

- **ci-cd.yml**: Runs on push to development branch and pull requests to main branches
- **build.yml**: Runs only on push to development branch
- **automated_integration_tests.yml**: Runs only on pull requests to testing, acceptance, and main branches (not on push events)

This configuration ensures that:
1. Development branch has continuous integration
2. Integration tests run only when needed (on pull requests to stable branches)
3. No duplicate workflow runs occur when creating or updating pull requests

## Running the Project

It's important to note that Maven is not required for simply running the Jabberpoint application. It's only needed if you want to:
- Build the project from source
- Run the automated tests
- Generate code coverage reports

For users who just want to run the application, a pre-built JAR file is available in the Releases section of the repository.
