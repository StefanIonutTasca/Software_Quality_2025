# Contributing to Jabberpoint

This document outlines how to contribute to the Jabberpoint project following our DTAP workflow.

## Getting Started

1. Make sure you're on the development branch:
   ```
   git checkout development
   ```

2. Pull the latest changes:
   ```
   git pull origin development
   ```

3. Create a new feature branch:
   ```
   git checkout -b feature/your-feature-name
   ```

## Development Guidelines

1. **Keep commits small and focused**: Each commit should represent a single logical change.

2. **Write meaningful commit messages**: Explain what changes you made and why.

3. **Follow code style conventions**:
   - Use meaningful variable and method names
   - Add JavaDoc comments to classes and methods
   - Use proper indentation and formatting

4. **Include tests**:
   - Write unit tests for new code
   - Ensure existing tests pass
   - Aim for high code coverage

## Code Quality

Before submitting your changes:

1. Run tests locally:
   ```
   mvn clean test
   ```

2. Check code coverage:
   ```
   mvn jacoco:report
   ```
   Then open `target/site/jacoco/index.html` in a browser

3. Fix any failing tests or coverage issues

## Submitting Your Changes

1. Push your feature branch:
   ```
   git push -u origin feature/your-feature-name
   ```

2. Create a Pull Request to the `development` branch

3. Wait for code review and approval

4. After approval, your changes will be merged to `development`

## DTAP Progression

Once enough features accumulate in `development`:

1. Changes move to `testing` for thorough testing
2. After testing, they move to `acceptance` for stakeholder review
3. Finally, approved changes are merged to `main` for production release

## Handling Merge Conflicts

If you encounter merge conflicts:

1. Pull the latest changes from the target branch:
   ```
   git checkout development
   git pull origin development
   ```

2. Switch back to your feature branch and merge:
   ```
   git checkout feature/your-feature-name
   git merge development
   ```

3. Resolve conflicts, then commit and push the resolved changes
