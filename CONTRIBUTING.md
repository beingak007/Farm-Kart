# Contributing to Farm Kart Microservices

Thank you for considering a contribution to Farm Kart! This document outlines the process for contributing code, reporting bugs, and proposing features.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Branch Strategy](#branch-strategy)
- [Commit Message Convention](#commit-message-convention)
- [Pull Request Process](#pull-request-process)
- [Code Quality Standards](#code-quality-standards)
- [Testing Requirements](#testing-requirements)

---

## Code of Conduct

By participating in this project you agree to maintain a respectful, inclusive, and collaborative environment. Harassment or discrimination of any kind will not be tolerated.

---

## How to Contribute

### Reporting Bugs

1. Search [existing issues](../../issues) to avoid duplicates.
2. Open a new issue using the **Bug Report** template.
3. Include reproduction steps, expected vs. actual behaviour, and your environment.

### Suggesting Features

1. Open an issue using the **Feature Request** template.
2. Describe the use case and why it belongs in the platform.
3. Wait for maintainer approval before starting implementation.

### Submitting Code

1. Fork the repository.
2. Create a feature branch from `develop` (see Branch Strategy below).
3. Implement your changes following the Code Quality Standards.
4. Open a Pull Request targeting `develop`.

---

## Development Setup

### Prerequisites

| Tool | Version |
|------|---------|
| Java | 21 |
| Maven | 3.9+ |
| Docker | 24+ |
| Docker Compose | 2.x |

### Local Startup

```bash
# Start infrastructure + all services (MySQL, Redis, Kafka, apps)
./dev-local-startup.sh

# Build all Java modules (reactor root is microservice_boot/)
cd microservice_boot
mvn clean install -DskipTests

# Run the main app directly
cd microservice_boot/marketplace/marketplace-rest
mvn spring-boot:run
```

---

## Branch Strategy

We follow **GitFlow**:

```
main          — production releases only
develop       — integration branch (PRs merge here)
feature/*     — new features (branch from develop)
fix/*         — bug fixes (branch from develop)
hotfix/*      — critical prod fixes (branch from main)
release/*     — release candidates (branch from develop)
```

### Naming Convention

```
feature/ISSUE-123-add-farmer-registration
fix/ISSUE-456-fix-order-status-update
hotfix/ISSUE-789-fix-payment-gateway-timeout
release/v1.2.0
```

---

## Commit Message Convention

We follow the [Conventional Commits](https://www.conventionalcommits.org/) specification:

```
<type>(<scope>): <short description>

[optional body]

[optional footer: ISSUE-123]
```

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation only |
| `style` | Formatting, no logic change |
| `refactor` | Code change without new feature or fix |
| `test` | Adding or updating tests |
| `chore` | Build process, tooling |
| `perf` | Performance improvement |
| `ci` | CI/CD changes |

### Examples

```
feat(farmer): add farmer KYC document upload endpoint
fix(buyer): resolve null pointer on cart checkout
docs(api): update OpenAPI spec for logistics endpoints
test(warehouse): add unit tests for inventory allocation service
ci: upgrade JaCoCo to 0.8.12
```

---

## Pull Request Process

1. Ensure your branch is up to date with `develop`.
2. All CI checks must pass (build, tests, SonarQube Quality Gate, JaCoCo coverage ≥ 80%).
3. At least **1 reviewer approval** is required before merging.
4. Use **Squash and Merge** for feature branches.
5. Delete the branch after merging.

---

## Code Quality Standards

The following tools run automatically in CI and will fail the build if violated:

| Tool | Purpose | Threshold |
|------|---------|-----------|
| JaCoCo | Code coverage | ≥ 80% |
| SonarQube | Quality Gate | Pass |
| Checkstyle | Style compliance | 0 violations |
| SpotBugs | Bug patterns | 0 high/medium bugs |
| PMD | Code analysis | 0 priority 1–3 violations |
| OWASP Dependency Check | Security vulnerabilities | CVSS < 7 |

---

## Testing Requirements

- Every new service method must have at least one unit test.
- Integration tests must use `@SpringBootTest` with Testcontainers for external dependencies.
- No production code committed without corresponding tests.
- Mocking: use **Mockito** for unit tests.
- Assertions: use **AssertJ** fluent assertions.

---

Thank you for helping make Farm Kart better!
