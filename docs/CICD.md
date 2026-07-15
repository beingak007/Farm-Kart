# Farm Kart — CI/CD Pipeline Documentation

Enterprise-grade GitHub Actions pipeline for the Farm Kart platform
(Java 21 · Spring Boot 3.3 · Maven multi-module · 3 deployables:
`farm-kart-app` :8080, `farm-kart-notification` :8087, `farm-kart-agent` :8091).

---

## 1. Pipeline at a glance

```
Pull Request → pr-checks.yml (orchestrator)
   ├── build.yml              Compile + package + jar artifacts        ┐
   ├── test.yml               Unit + IT tests, JaCoCo, Checkstyle,     │ parallel
   │                          PMD, SpotBugs                            │
   ├── security.yml           Gitleaks + Dependency Review/License     │
   │                          + CodeQL                                 │
   ├── dependency-check.yml   OWASP (CVSS ≥ 7 fails)                   ┘
   ├── sonar.yml              SonarCloud + Quality Gate wait   (needs test)
   ├── docker.yml             3 images, build-only on PR        (needs build)
   ├── quality-gate           ONE aggregated required status check
   ├── pr-comment             ✅/❌ summary comment on the PR
   └── notify.yml             Email + Slack + Teams on failure

Push develop → same pipeline + docker push → deploy-dev.yml  → dev
Push main    → same pipeline + docker push → deploy-stage.yml → stage
Manual       → deploy-qa.yml → qa
Manual + approval + typed confirmation → deploy-prod.yml → production
Any time     → rollback.yml (manual, any environment)
Weekly       → security-scan.yml (OWASP, opens issue on failure)
```

A PR can only merge when the **`Quality Gate`** status check is green —
enforced by branch protection (see §6).

---

## 2. Workflow reference

### Reusable workflows (`on: workflow_call`)

| File | What it does | Fails when |
|---|---|---|
| `build.yml` | `mvn clean package -DskipTests` on all modules; uploads the 3 runnable jars as artifacts | Compilation error |
| `test.yml` | `mvn verify -P quality` — Surefire unit tests, Failsafe integration tests (`*IT.java`), JaCoCo reports, Checkstyle + PMD + SpotBugs; publishes JUnit results as a check | Any test fails, or a static-analysis violation exceeds the pom thresholds |
| `sonar.yml` | `mvn verify sonar:sonar -Dsonar.qualitygate.wait=true` — full SonarCloud scan with coverage; **blocks until the Quality Gate is evaluated**. Skips with a warning when `SONAR_TOKEN` is absent | Sonar gate red: coverage < 80%, bugs > 0, vulnerabilities > 0, rating below A |
| `security.yml` | 3 parallel jobs: **Gitleaks** (secrets in full git history), **Dependency Review** (new vulnerable deps + license compliance, PR-only), **CodeQL** (Java SAST → Security tab) | Secret found; high-severity dep introduced; denied license (GPL/AGPL/LGPL) |
| `dependency-check.yml` | OWASP Dependency Check via the `security-scan` Maven profile; NVD database cached between runs | Any dependency with CVSS ≥ 7 (suppressions: `owasp-suppressions.xml`) |
| `docker.yml` | Matrix build of the 3 images with BuildKit layer caching; pushes to GHCR on branch builds only | Dockerfile/build error |
| `notify.yml` | Failure fan-out: HTML email (developer + tech lead), Slack blocks, Teams MessageCard. Every channel is optional — skipped when its secret is missing | Never (notification-only) |
| `_deploy.yml` | SSH deploy shared by all environments: record current tag → pull `sha-` images → `docker compose up -d` → health/liveness/readiness verification → **auto-rollback + notify on failure** | Deploy error or failed verification |

### Entry-point workflows

| File | Trigger | Notes |
|---|---|---|
| `pr-checks.yml` | PRs to `main`/`develop`; pushes to `main`, `develop`, `release/**`, `hotfix/**`; manual | The orchestrator. `concurrency` cancels superseded runs. Aggregates everything into the `Quality Gate` check, comments on the PR, calls `notify.yml` on failure |
| `deploy-dev.yml` | Auto after a green `PR Checks` run on `develop`; manual | Deploys `sha-<head-sha>` to the `dev` environment |
| `deploy-qa.yml` | Manual only | QA team pulls a specific tag when ready to test |
| `deploy-stage.yml` | Auto after a green `PR Checks` run on `main`; manual | Release-candidate environment |
| `deploy-prod.yml` | **Manual only** | Two-man rule: typed phrase `deploy-to-production` **and** Required Reviewers on the `production` GitHub Environment |
| `rollback.yml` | Manual | Re-deploys a known-good `sha-` tag to any environment via `_deploy.yml`; always notifies the team |
| `security-scan.yml` | Weekly cron (Sun 02:00 UTC); manual | Reuses `dependency-check.yml`; opens a GitHub issue on failure |

### Composite action

`.github/actions/setup-build-env/action.yml` — Temurin Java 21 +
Maven repository cache (`cache: maven`, keyed on `**/pom.xml` hashes).
Used by every Java job so the JDK version is bumped in exactly one place.

---

## 3. Third-party actions used & why

| Action | Purpose |
|---|---|
| `actions/checkout@v4` | Clone the repo (`fetch-depth: 0` where git history is needed: Sonar, Gitleaks) |
| `actions/setup-java@v4` | Temurin JDK + built-in Maven cache |
| `actions/cache@v4` | Sonar package cache, OWASP NVD database cache |
| `actions/upload-artifact@v4` | Jars, JaCoCo/Surefire/SpotBugs/PMD/OWASP reports |
| `dorny/test-reporter@v1` | Renders JUnit XML as a readable GitHub check |
| `gitleaks/gitleaks-action@v2` | Secret scanning over full history |
| `actions/dependency-review-action@v4` | New-dependency CVEs + license compliance on PR diffs |
| `github/codeql-action@v3` | Java SAST, results in the Security tab |
| `docker/setup-buildx-action@v3`, `docker/login-action@v3`, `docker/metadata-action@v5`, `docker/build-push-action@v6` | BuildKit builds, GHCR login, consistent tagging, layer caching (`type=gha`) |
| `appleboy/ssh-action@v1.2.0` | Remote deployment over SSH |
| `dawidd6/action-send-mail@v3` | SMTP failure emails |
| `actions/github-script@v7` | PR status comment, auto-created security issues |

---

## 4. Secrets

Configure under **Settings → Secrets and variables → Actions** (repo level),
and per-environment under **Settings → Environments** for deploy secrets.

| Secret | Scope | Used by | Purpose |
|---|---|---|---|
| `SONAR_TOKEN` | Repo | `sonar.yml` | SonarCloud auth (User → My Account → Security). Scan is skipped if absent |
| `NVD_API_KEY` | Repo | `dependency-check.yml` | Free key from nvd.nist.gov — without it NVD downloads are throttled (30+ min scans) |
| `SMTP_SERVER` / `SMTP_PORT` / `SMTP_USERNAME` / `SMTP_PASSWORD` | Repo | `notify.yml` | Outbound mail server (e.g. `smtp.gmail.com:587` with an app password) |
| `EMAIL_DEVELOPER` / `EMAIL_TECH_LEAD` | Repo | `notify.yml` | Failure email recipients |
| `SLACK_WEBHOOK_URL` | Repo | `notify.yml` | Slack Incoming Webhook of the alerts channel |
| `TEAMS_WEBHOOK_URL` | Repo | `notify.yml` | Teams Incoming Webhook connector |
| `DEPLOY_HOST` / `DEPLOY_USER` / `DEPLOY_SSH_KEY` | Per environment | `_deploy.yml` | SSH target for each environment (different host per env) |
| `APP_BASE_URL` (**variable**, not secret — Settings → Environments → Variables) | Per environment | `_deploy.yml` | Public base URL used by health verification, e.g. `https://dev.farmkart.com` |
| `GITHUB_TOKEN` | Automatic | docker/GHCR, CodeQL, PR comments | Built-in token; no setup needed |

**Every optional secret degrades gracefully** — a missing webhook or Sonar
token produces a `::warning::`/`::notice::`, never a failed pipeline.

---

## 5. GitHub Environments

Create under **Settings → Environments**: `dev`, `qa`, `stage`, `production`.

| Environment | Protection rules |
|---|---|
| `dev` | None — auto-deploy from `develop` |
| `qa` | Optional: restrict to QA team |
| `stage` | Optional: 1 required reviewer |
| `production` | **Required reviewers (Tech Lead + one senior engineer)**, deployment branch policy → `main` only, optional wait timer |

The `environment:` key in `_deploy.yml` is what pauses the production job
until an approver clicks **Approve** — this is the manual-approval gate.

---

## 6. Branch protection (main + develop)

Apply with `./scripts/setup-branch-protection.sh` (needs `gh` + admin):

| Rule | Value | Why |
|---|---|---|
| Require pull request | ✅ | No direct pushes — all code is reviewed |
| Required approvals | **2** | Two independent reviewers |
| Code Owner review | ✅ | Folder experts must sign off (CODEOWNERS) |
| Dismiss stale reviews | ✅ | New commits invalidate old approvals |
| Require last-push approval | ✅ | Author can't self-approve their final push |
| Required status check | `Quality Gate` | The single aggregated check from `pr-checks.yml` — build, tests, static analysis, Sonar, security, OWASP, Docker must ALL pass |
| Strict (up-to-date branch) | ✅ | Prevents "semantic conflicts" — the PR is re-validated against the latest base |
| Conversation resolution | ✅ | Every review thread resolved before merge |
| Force pushes | ❌ blocked | History is immutable |
| Deletions | ❌ blocked | Protected branches can't be deleted |
| Enforce for admins | ✅ | No back doors |

Merge conflicts are inherently blocked by GitHub (a conflicted PR has no merge button).

---

## 7. Quality gates & thresholds

| Gate | Tool | Threshold | Where configured |
|---|---|---|---|
| Compilation | Maven | 0 errors | — |
| Unit/Integration tests | Surefire/Failsafe | 0 failures | — |
| Coverage | JaCoCo → Sonar | **≥ 80%** | `jacoco.minimum.coverage` in root pom + Sonar gate |
| Code style | Checkstyle | severity ≥ warning fails | `checkstyle.xml`, root pom |
| Bug patterns | SpotBugs | Medium+ effort Max fails | `spotbugs-exclude.xml`, root pom |
| Code smells | PMD | priority ≥ 3 fails | `pmd-ruleset.xml`, root pom |
| Dependencies | OWASP | CVSS ≥ 7 fails | `<failBuildOnCVSS>7`, `owasp-suppressions.xml` |
| Secrets | Gitleaks | any finding fails | default ruleset |
| Licenses | Dependency Review | GPL/AGPL/LGPL denied | `security.yml` |
| SAST | CodeQL | security-and-quality queries | `security.yml` |
| Sonar gate | SonarCloud | Coverage ≥ 80%, Bugs = 0, Vulnerabilities = 0, Security/Reliability rating A, hotspots 100% reviewed | SonarCloud → Quality Gates (UI) + `-Dsonar.qualitygate.wait=true` |

### Maven plugins involved (root `pom.xml`)

| Plugin | Role |
|---|---|
| `jacoco-maven-plugin` | Instruments tests (`prepare-agent`), writes XML/HTML reports (`report`), enforces the 80% bundle minimum (`check`) |
| `maven-checkstyle-plugin` | Style rules from `checkstyle.xml`, runs at `validate` |
| `spotbugs-maven-plugin` | Bytecode bug patterns, runs at `verify` |
| `maven-pmd-plugin` | Source-level smells from `pmd-ruleset.xml`, runs at `verify` |
| `dependency-check-maven` | OWASP CVE scan, `security-scan` profile |
| `sonar-maven-plugin` | Uploads analysis + coverage to SonarCloud |
| `spring-boot-maven-plugin` | Repackages the 3 runnable jars (only on the 3 deployable `-rest` modules) |

---

## 8. Docker image strategy

Images (GHCR): `ghcr.io/<owner>/<repo>/{farm-kart-app, farm-kart-notification, farm-kart-agent}`

| Tag | When | Use |
|---|---|---|
| `sha-<full-sha>` | every branch build | **Immutable** — what deploy & rollback reference |
| `develop`, `main` | branch pushes | Moving pointers for humans |
| `latest` | default branch only | Convenience |
| `v1.2.3`, `1.2` | git tags | Releases |

PR builds compile the images but **don't push** — validates Dockerfiles
without polluting the registry. Layer cache is shared through the GitHub
Actions cache backend (`cache-from/to: type=gha`).

---

## 9. Deployment, verification & rollback

1. `_deploy.yml` SSHes to the environment host, records the currently
   deployed tag into `.previous_tag`, pulls the new `sha-` images and
   runs `docker compose up -d`.
2. **Verification** (from the runner, against `APP_BASE_URL`):
   `GET /farm-kart/actuator/health`, `/health/liveness`, `/health/readiness`
   — 30 retries × 10 s each. Probes are enabled in all three apps via
   `management.endpoint.health.probes.enabled: true`.
3. **Auto-rollback**: if verification fails, the workflow re-deploys
   `.previous_tag` on the same host and then fails the run, which triggers
   `notify.yml` (email + Slack + Teams).
4. **Manual rollback** (`rollback.yml`): for regressions found later —
   dispatch with environment + last known-good `sha-` tag; the run reuses
   the exact same deploy + verification logic.

Server prerequisite (one-time per host): `/opt/farmkart/docker-compose.yml`
that references the three images with `${IMAGE_TAG}`, e.g.
`image: ghcr.io/<owner>/<repo>/farm-kart-app:${IMAGE_TAG}`.

---

## 10. Reviewer auto-assignment (CODEOWNERS)

`CODEOWNERS` maps folders to owners; GitHub auto-requests reviews from the
matching owners on every PR. Role-based rules at the end of the file
(last match wins):

| Pattern | Owner (target team) |
|---|---|
| `**/src/main/java/` | Backend Team |
| `**/src/test/java/` | QA Team |
| `**/db/migration/`, `**/cassandra/migration/` | DBA Team |
| `/.github/workflows/` | DevOps Team |

Team slugs (`@farmkart/backend-team` …) are commented placeholders until a
GitHub organisation with those teams exists; `@beingakstyle` is the
effective owner meanwhile.

---

## 11. Environment variables used by pipelines

| Variable | Where | Purpose |
|---|---|---|
| `MAVEN_OPTS=-Xmx2g -XX:MaxMetaspaceSize=512m` | all Maven jobs | Prevents OOM in the large multi-module reactor |
| `SPRING_PROFILES_ACTIVE=test` | test/sonar jobs | Keeps tests off dev/prod config |
| `IMAGE_TAG` | deploy hosts | Consumed by the server-side compose file |
| `NVD_API_KEY`, `SONAR_TOKEN`, `GITHUB_TOKEN` | see §4 | Tool auth |

---

## 12. Setup checklist (one-time)

1. **SonarCloud**: import the repo at sonarcloud.io → create `SONAR_TOKEN`
   secret → configure the Quality Gate conditions from §7.
2. **NVD**: request a free API key → `NVD_API_KEY` secret.
3. **Notifications**: create Slack/Teams webhooks + SMTP app password →
   secrets from §4 (all optional).
4. **Environments**: create `dev`/`qa`/`stage`/`production` with the
   protection rules from §5 and per-environment deploy secrets.
5. **Branch protection**: run `./scripts/setup-branch-protection.sh`.
6. **GitHub security features** (Settings → Code security): enable Secret
   scanning + Push protection + Dependabot alerts (`dependabot.yml` is
   already committed).
7. **Deploy hosts**: install Docker + compose file per §9.
