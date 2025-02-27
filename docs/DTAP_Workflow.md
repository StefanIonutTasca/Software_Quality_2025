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
4. (Future) Deployment to appropriate environments based on the branch
