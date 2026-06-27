---
name: Pull Request
about: Submit code changes for review
---

## Summary

<!-- Provide a concise description of WHAT changed and WHY. -->

Fixes # <!-- Issue number, e.g., Fixes #123 -->

---

## Type of Change

<!-- Check all that apply. -->

- [ ] `feat` — New feature
- [ ] `fix` — Bug fix
- [ ] `docs` — Documentation update
- [ ] `refactor` — Code refactor (no functional change)
- [ ] `test` — Adding or updating tests
- [ ] `perf` — Performance improvement
- [ ] `ci` — CI/CD pipeline change
- [ ] `chore` — Build tooling / dependency update
- [ ] `security` — Security fix

---

## Affected Microservice(s)

<!-- List the microservices modified by this PR. -->

- [ ] farm-kart (Core)
- [ ] farm-kart-farmer
- [ ] farm-kart-buyer
- [ ] farm-kart-product-catalog
- [ ] farm-kart-logistics
- [ ] farm-kart-warehouse
- [ ] farm-kart-market-price
- [ ] farm-kart-notification
- [ ] farm-kart-admin
- [ ] farm-kart-reporting
- [ ] farm-kart-ai-advisory
- [ ] farm-kart-agent
- [ ] farm-kart-starter-common / farm-kart-framework (Shared)
- [ ] farm-kart-ui (Frontend)
- [ ] Infrastructure / CI

---

## Changes Made

<!-- Bullet-point list of key changes. Be specific. -->

-
-
-

---

## API Changes

<!-- If you modified any REST API endpoints, document them here. -->

| Method | Path | Change |
|--------|------|--------|
| | | |

---

## Database Changes

<!-- Any schema migrations, new tables, new columns? -->

- [ ] No database changes
- [ ] New migration added (Flyway/Liquibase)

---

## Testing

<!-- Describe how you tested these changes. -->

- [ ] Unit tests added / updated
- [ ] Integration tests added / updated
- [ ] Manually tested locally

**Test Coverage:** <!-- e.g., "Coverage increased from 72% to 85%" -->

---

## Quality Gate Checks

<!-- These must all be green before merging. -->

- [ ] Build passes (`mvn clean install`)
- [ ] JaCoCo coverage ≥ 80%
- [ ] SonarQube Quality Gate: Pass
- [ ] Checkstyle: 0 violations
- [ ] SpotBugs: 0 high/medium bugs
- [ ] OWASP Dependency Check: No CVSS ≥ 7 vulnerabilities

---

## Breaking Changes

- [ ] This PR introduces breaking changes.

<!-- If yes, describe what breaks and what consumers need to update. -->

---

## Deployment Notes

<!-- Any special steps required during or after deployment? -->

- [ ] No special deployment steps required
- [ ] Environment variables added (document below)
- [ ] Docker image rebuild required
- [ ] Database migration required

---

## Screenshots / Logs (if applicable)

<!-- Attach relevant screenshots, curl output, or log snippets. -->

---

## Reviewer Notes

<!-- Anything specific you want reviewers to focus on? -->
