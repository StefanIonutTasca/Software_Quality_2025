# CI/CD Pipeline Documentation

This document describes the comprehensive CI/CD (Continuous Integration/Continuous Deployment) pipeline implemented for the Jabberpoint project.

## Overview

Our CI/CD pipeline is designed to ensure code quality, maintain test coverage, and automate the build and deployment process across the DTAP street. The pipeline uses GitHub Actions to implement various quality gates and automation steps.

## Workflow Files

### 1. `ci-cd.yml` - Main CI/CD Pipeline

The primary workflow that:
- Builds the project
- Runs tests
- Generates JaCoCo reports
- Uploads artifacts

### 2. `build.yml` - Multi-Java Build

Tests compatibility across multiple Java versions:
- Builds and tests on Java 11 and 17
- Archives build artifacts
- Archives test results

### 3. `google_linter.yml` - Code Formatting

Ensures consistent code formatting:
- Uses Google Java Format
- Automatically formats code
- Creates PRs for formatting changes

### 4. `code_check.yml` - Code Quality

Static code analysis:
- Integrates with SonarCloud (when configured)
- Analyzes code quality metrics
- Identifies code smells, bugs, and vulnerabilities

### 5. `labeler.yml` - PR Labeling

Automates PR management:
- Automatically labels PRs based on file changes
- Helps with PR categorization and prioritization

### 6. `automated_integration_tests.yml` - Integration Testing

Runs integration tests on specific branches:
- Executes integration tests on testing, acceptance, and main branches
- Uploads test results

### 7. `pullRequest.yml` - PR Validation

Ensures PR quality:
- Verifies PR has tests
- Validates code coverage
- Checks PR description

## Quality Gates

Our pipeline implements several quality gates that code must pass before progression through the DTAP street:

### 1. Build Success
- Code must compile without errors

### 2. Unit Tests
- All unit tests must pass
- Test coverage must meet minimum thresholds (70% instruction, 60% branch)

### 3. Code Formatting
- Code must follow Google Java Format standards

### 4. Integration Tests
- Integration tests must pass on testing branch and beyond

### 5. Code Quality
- (When SonarCloud is configured) Code must pass quality thresholds

## DTAP Branch Protection

Each branch in the DTAP street should have appropriate protection rules:

### development
- Require PR reviews before merging
- Require status checks to pass

### testing
- Require PR reviews before merging
- Require status checks to pass
- Require integration tests to pass

### acceptance
- Require PR reviews before merging
- Require integration tests to pass
- Require approval from stakeholders

### main (production)
- Require PR reviews from multiple reviewers
- Require all status checks to pass
- Restrict who can push to branch

## CI/CD Flow

1. Developers work on feature branches
2. PRs merge to `development`
3. Automated tests run on every commit
4. Changes batch-promotion to `testing`
5. Integration tests run on `testing`
6. After testing approval, promote to `acceptance`
7. After stakeholder approval, promote to `main`
8. Release built from `main`

## Tools and Plugins

- **JaCoCo**: Code coverage analysis
- **Maven Enforcer**: Dependency management enforcement
- **Maven Failsafe**: Integration test execution
- **GitHub Actions**: CI/CD workflow orchestration
- **SonarCloud**: Code quality analysis (when configured)
- **Google Java Format**: Consistent code formatting
