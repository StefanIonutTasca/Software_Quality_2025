# Jabberpoint Repair Project

This project aims to repair and improve the Jabberpoint presentation software as part of a school assignment.

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
- JaCoCo for code coverage reporting
- CI/CD pipeline for automated testing and deployment
