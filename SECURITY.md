# Security Policy — Farm Kart Microservices

## Supported Versions

| Version | Supported |
|---------|-----------|
| 1.x (latest) | ✅ |
| < 1.0 | ❌ |

---

## Reporting a Vulnerability

**Please do NOT open a public GitHub issue for security vulnerabilities.**

Instead, report them responsibly via one of the following methods:

### Option 1: GitHub Private Security Advisory (Preferred)

1. Go to the [Security tab](../../security/advisories) of this repository.
2. Click **"Report a vulnerability"**.
3. Fill in the details of the vulnerability.

### Option 2: Email

Send an email to: **beingakstyle@gmail.com**

Include the subject line: `[SECURITY] Farm Kart — <brief description>`

---

## What to Include in Your Report

Please provide as much detail as possible:

- **Description** of the vulnerability and its potential impact.
- **Steps to reproduce** (proof of concept if possible).
- **Affected component(s)** — which microservice, endpoint, or dependency.
- **Suggested fix** (optional but appreciated).
- **Your contact information** for follow-up.

---

## Response Timeline

| Milestone | Target |
|-----------|--------|
| Acknowledgement | Within 48 hours |
| Initial assessment | Within 5 business days |
| Fix timeline communicated | Within 10 business days |
| Patch released | Depends on severity (critical: 72h, high: 7d, medium/low: 30d) |
| Public disclosure | After patch is released and users are notified |

---

## Security Practices

This project follows these security practices:

- **Dependencies** are scanned automatically via OWASP Dependency Check on every CI run.
- **Secrets** are never committed — all credentials use GitHub Actions Secrets / environment variables.
- **SonarQube** Security Hotspot scanning runs on every pull request.
- **Dependabot** is configured to auto-create PRs for dependency updates with known CVEs.
- **Branch protection** is enforced — no direct pushes to `main` or `develop`.

---

## Scope

The following are **in scope** for security reports:

- Authentication / authorization bypass in any microservice.
- Injection vulnerabilities (SQL, command, JNDI, etc.).
- Sensitive data exposure (PII, credentials, financial data).
- Remote code execution.
- Insecure direct object references (IDOR).
- JWT-related vulnerabilities.

The following are **out of scope**:

- Vulnerabilities in development-only tooling (local Docker infra).
- Social engineering attacks.
- Issues requiring physical access.
- Denial of service without significant business impact.

---

Thank you for helping keep Farm Kart and its users safe.
