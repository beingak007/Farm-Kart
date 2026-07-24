# Farm Kart — Farmer Digital Marketplace Platform

[![CI](https://github.com/beingakstyle/farm-kart-microservice/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/beingakstyle/farm-kart-microservice/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=beingakstyle_farm-kart-microservice&metric=alert_status)](https://sonarcloud.io/project/overview?id=beingakstyle_farm-kart-microservice)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=beingakstyle_farm-kart-microservice&metric=coverage)](https://sonarcloud.io/project/overview?id=beingakstyle_farm-kart-microservice)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=beingakstyle_farm-kart-microservice&metric=bugs)](https://sonarcloud.io/project/overview?id=beingakstyle_farm-kart-microservice)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=beingakstyle_farm-kart-microservice&metric=vulnerabilities)](https://sonarcloud.io/project/overview?id=beingakstyle_farm-kart-microservice)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=beingakstyle_farm-kart-microservice&metric=security_rating)](https://sonarcloud.io/project/overview?id=beingakstyle_farm-kart-microservice)
[![Java Version](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

A production-ready modular-monolith platform (Java 21 + Spring Boot 3.3.5) connecting farmers directly with buyers, logistics partners, warehouses, and financial institutions.

**Architecture (2026-07):** the platform runs as **3 deployables** instead of 12. All domain modules (marketplace, farmer, buyer, logistics, warehouse, market-price, admin, product-catalog, reporting, ai-advisory) are merged into the single **Farm Kart app** on port 8080. Only two real microservices remain separate: **Notification** (async Kafka consumer + external SMS/Email/Push providers, port 8087) and **Agent** (LLM/OpenAI workload, port 8091). Domain code still lives in its own Maven modules (`-client`/`-repository`/`-service`/`-rest`), so module boundaries and ownership stay intact — only the deployment units were consolidated to cut maintenance overhead.

---

## Platform Engineering (API + Events)

Recent senior-architecture changes applied across all microservices. Source modules are listed so you know **where** each piece lives.

### Standard API envelope (no internal errors exposed)

All REST services return the same JSON contract. Internal stack traces, SQL, and provider errors never reach the client.

| Component | Module / path |
|---|---|
| `ApiResponse`, `ApiError`, `ApiErrorCode`, `FieldErrorDetail`, `ResponseMeta` | `starter-common/.../dto/` |
| `BusinessException` (safe user-facing messages + error codes) | `starter-common/.../exception/` |
| `RequestContext` (correlation ID via MDC) | `starter-common/.../context/` |
| `RequestIdFilter` (`X-Request-Id` on every request/response) | `common-rest/.../filter/` |
| `GlobalExceptionHandler` (validation, 404, 409, 500 — safe messages only) | `common-rest/.../exception/` |
| `ApiResponseBodyAdvice` (adds `meta.requestId` + `meta.timestamp` on success) | `common-rest/.../advice/` |
| UI client — `ApiClientError`, `parseApiResponse()` | `farm-kart-ui/src/api/errors.js`, `client.js` |
| MCP client — `FarmKartApiError`, typed `unwrap()` | `farm-kart-mcp/src/clients/api.ts`, `types/index.ts` |

**Success response example:**

```json
{
  "success": true,
  "data": { "id": 101 },
  "meta": { "requestId": "uuid", "timestamp": "2026-06-30T10:00:00Z" }
}
```

**Error response example (UI-safe):**

```json
{
  "success": false,
  "message": "Farmer not found for userId: 42",
  "error": { "code": "RESOURCE_NOT_FOUND", "message": "Farmer not found for userId: 42" },
  "meta": { "requestId": "uuid", "timestamp": "2026-06-30T10:00:00Z" }
}
```

**Validation errors** include `error.details[]` with `{ "field", "message", "code" }` for form-level UI display.

---

### Event-driven architecture (Kafka)

Events publish **after DB commit** via `DomainEventPublisher` — no ghost events on rollback.

| Component | Module / path |
|---|---|
| `FkTopics`, `FkBaseEvent`, domain event records | `starter-common/.../events/` |
| `DomainEventPublisher` (transaction-aware publish) | `starter-common/.../events/` |
| `FkKafkaAutoConfiguration`, `EventProcessingGuard` | `starter-common/.../configuration/` |
| `FkKafkaConsumerConfiguration` (`@EnableKafka`) | `starter-common/.../events/` |
| `PlatformAuditConsumer` (writes audit logs from domain events) | `admin/.../service/` |
| `DomainNotificationConsumer` (welcome, order, payment, delivery alerts) | `notification/.../service/` |
| `DurableEventGuard` + `processed_domain_events` table (idempotency) | `admin`, `notification` |

**Event flow example (farmer onboard):**

```
POST /marketplace/api/v1/farmers/onboard
  → DB save
  → Kafka: farmkart.farmer.created
  → Admin module (same app): audit log
  → Notification service: welcome PUSH/SMS
```

**Producers (who publishes what):**

| Producer | Topics published |
|---|---|
| Farm Kart App (8080) — Marketplace domain | `user.registered`, `order.created`, `order.cancelled`, `payment.success`, `payment.failed`, `sheet.uploaded` |
| Farm Kart App (8080) — Farmer domain | `farmer.created`, `farmer.verified` |
| Farm Kart App (8080) — Buyer domain | `buyer.created` |
| Farm Kart App (8080) — Logistics domain | `shipment.created`, `shipment.delivered` |
| Farm Kart App (8080) — Warehouse domain | `warehouse.booked` |
| Farm Kart App (8080) — Market Price domain | `market.price.updated` |
| Farm Kart App (8080) — Product Catalog domain | `crop.listed` |

**Consumers:**

| Consumer | Listens to | Action |
|---|---|---|
| Farm Kart App (8080) — Admin module | All major domain topics | Persist `audit_logs` |
| Notification (8087) | `user.registered`, `farmer.*`, `order.created`, `payment.success`, `shipment.delivered` | SMS / Push / Email |
| Notification (8087) | `notification.triggered` | Template-based dispatch (existing) |

**Developer rule:** use `DomainEventPublisher.publish(topic, key, event)` in services — not raw `KafkaTemplate.send()`.

---

## Architecture Overview

```
  React UI  ─────────────────────────────────── port 5173
       │
  ┌────┴────────────────────────────────────────────────────┐
  │             Farm Kart App (modular monolith)  :8080      │
  │                                                          │
  │  Marketplace · Farmer · Buyer · Logistics · Warehouse    │
  │  Market Price · Admin · Product Catalog · Reporting      │
  │  AI Advisory                                             │
  └──────────────────────────┬──────────────────────────────┘
                             │ Kafka (Event Bus)
        ┌────────────────────┴──────────────┐
        │                                   │
  Notification :8087                  Agent :8091
  (SMS/Email/Push consumer)           (LLM / OpenAI)
        │                                   │
  ┌─────┴───────────────────────────────────┴───────────────┐
  │                  Data Layer                              │
  │  MySQL (farmkart + notification + agent DBs)             │
  │  PostgreSQL (framework + warehouse-app)                  │
  │  Redis (cache/sessions)   Kafka Cluster   AWS S3         │
  └─────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
FARM_CART_MICROSERVICE/
│
├── microservice_boot/                    ← ALL Java services live here (Maven reactor root)
│   ├── pom.xml                           ← Root Maven POM (Java 21)
│   ├── checkstyle.xml / pmd-ruleset.xml / spotbugs-exclude.xml / owasp-suppressions.xml
│   │
│   ├── starter-common/         ← Shared foundation
│   │   ├── ApiResponse, ApiError, ApiErrorCode, BusinessException
│   │   ├── DomainEventPublisher, FkKafkaAutoConfiguration
│   │   ├── FkTopics.java                 All Kafka topic constants
│   │   ├── FkCacheNames.java             All Redis cache key constants
│   │   ├── RedisConfig.java              Shared Redis/cache configuration
│   │   └── Domain events (FarmerCreatedEvent, OrderCreatedEvent, UserRegisteredEvent, etc.)
│   │
│   ├── common-rest/            ← REST config, exception handling, request tracing
│   │   ├── GlobalExceptionHandler        Safe API errors (no internal leaks)
│   │   ├── RequestIdFilter               X-Request-Id correlation
│   │   └── ApiResponseBodyAdvice         meta on every response
│   ├── framework/              ← Dynamic view/model metadata engine
│   │
│   ├── marketplace/              ← Farm Kart App (runnable, port 8080)
│   │                                boots ALL domain modules below
│   │   ├── marketplace-client
│   │   ├── marketplace-repository
│   │   ├── marketplace-services
│   │   └── marketplace-rest
│   ├── farmer/                   ← Farmer domain module     (merged into :8080)
│   │   ├── farmer-client / farmer-repository / farmer-services / farmer-rest
│   ├── buyer/                    ← Buyer domain module      (merged into :8080)
│   ├── logistics/                ← Logistics domain module  (merged into :8080)
│   ├── warehouse/                ← Warehouse domain module  (merged into :8080)
│   ├── market-price/             ← Market Price domain module (merged into :8080)
│   ├── admin/                    ← Admin domain module      (merged into :8080)
│   ├── product-catalog/          ← Product Catalog domain module (merged into :8080)
│   ├── reporting/                ← Reporting domain module  (merged into :8080)
│   ├── ai-advisory/              ← AI Advisory domain module (merged into :8080)
│   │
│   ├── notification/           ← Notification Service (separate, port 8087)
│   ├── agent/                  ← AI Agent Service     (separate, port 8091)
│   │
│   ├── discovery-service/      ← Eureka registry      (port 8084)
│   │   └── discovery-service-rest/
│   ├── api-gateway/            ← Spring Cloud Gateway (port 8079)
│   │   └── api-gateway-rest/
│   │
│   ├── complex-migration-tracker/  ← Multi-step DB migrations (SQL+CQL+ES)
│   └── elasticsearch-manager/      ← Elasticsearch index mappings
│
├── farm-kart-ui/                         ← React frontend          (port 5173)
├── farm-kart-mcp/                        ← MCP server (TypeScript)
├── docker/ · infra/ · scripts/ · docs/   ← Infra, deploy templates, tooling, docs
└── .github/                              ← CI/CD workflows
```

> Java build commands ab `microservice_boot/` ke andar chalte hain:
> `cd microservice_boot && mvn clean package`

Each domain follows the Farm Kart-style **4-layer** layout (no `farm-kart-` prefix on folders):
```
{service}/                         e.g. admin/, farmer/, marketplace/
├── {service}-client/              DTOs, enums, request/response contracts
├── {service}-repository/          JPA entities, repositories, Flyway migrations
├── {service}-services/            Business logic, Kafka producers/consumers
└── {service}-rest/                Controllers (plain jar; merged domains have no own main class)
```

Only `marketplace`, `notification`, `agent`, `discovery-service` and `api-gateway` produce runnable Spring Boot jars.

---

## Services & Responsibilities

**Deployables (5):**

| Deployable | Port | Role |
|---|---|---|
| **discovery-service** | 8084 | Netflix Eureka registry (Farm Kart-style) |
| **api-gateway** | 8079 | Spring Cloud Gateway — single entry (`/farm-kart/**`, `/notification-service/**`, `/agent-service/**`) via `lb://` |
| **Farm Kart App** (`marketplace`) | 8080 | Marketplace + merged domains (MySQL `farmkart` + Postgres) |
| **Notification** | 8087 | SMS/Email/Push consumers |
| **Agent** | 8091 | LLM agent service |

Gateway traffic example: `http://localhost:8079/farm-kart/swagger-ui.html` → Eureka → marketplace `:8080`.
Without Eureka locally: `EUREKA_ENABLED=false GATEWAY_STATIC=1 ./dev-local-startup.sh`.

**Domain modules inside the Farm Kart App (all on :8080, context path `/farm-kart`):**

| Domain | API base | Key Features |
|---|---|---|
| **Marketplace** | `/api/v1/auth`, `/orders`, `/payments`, `/products`, `/vendors`, `/sheets` | Auth (JWT/OAuth2), Orders, Payments, S3 uploads, **publishes user/order/payment/sheet events** |
| **Farmer** | `/api/v1/farmers` | Farmer onboarding, farm details, verification, state/district search |
| **Buyer** | `/api/v1/buyers` | Buyer onboarding (Individual/Retailer/Exporter), profile management |
| **Logistics** | `/api/v1/shipments` | Shipment creation, tracking number, status updates |
| **Warehouse** | `/api/v1/warehouses` | Warehouse listing, cold storage, booking with auto cost calc |
| **Market Price** | `/api/v1/market-prices` | Mandi rate ingestion, latest prices, historical analytics |
| **Admin** | `/api/v1/admin/audit-logs` | Audit log storage, **Kafka audit consumer** (`PlatformAuditConsumer`) |
| **Product Catalog** | `/api/v1/catalog` | Crop categories, crop CRUD, full-text search, organic filter |
| **Reporting** | `/api/v1/reports` | Async report jobs (SALES, ORDER_SUMMARY, FARMER_ACTIVITY, etc.) |
| **AI Advisory** | `/api/v1/ai-advisory` | Crop recommendations by season/soil, demand & price forecasting |

---

## Event-Driven Architecture (Kafka)

All topics are centralised in `FkTopics.java` (`starter-common`).

### Topic registry

```
farmkart.user.registered          farmkart.farmer.created
farmkart.farmer.verified          farmkart.buyer.created
farmkart.crop.listed              farmkart.crop.updated
farmkart.order.created            farmkart.order.confirmed
farmkart.order.cancelled          farmkart.order.delivered
farmkart.payment.initiated        farmkart.payment.success
farmkart.payment.failed           farmkart.shipment.created
farmkart.shipment.delivered       farmkart.warehouse.booked
farmkart.market.price.updated     farmkart.notification.triggered
farmkart.sheet.uploaded           farmkart.audit.log.created
```

### Publish / consume pattern

```
┌─────────────┐   afterCommit    ┌───────────────┐   consume   ┌──────────────────┐
│  Service    │ ───────────────► │ Kafka (topic) │ ──────────► │ Admin / Notif    │
│  (producer) │ DomainEventPub.  │ FkBaseEvent   │  idempotent │ (consumer)       │
└─────────────┘                  └───────────────┘             └──────────────────┘
```

- **Publish:** `DomainEventPublisher` in `starter-common` — called from service layer after DB save.
- **Consume:** `@KafkaListener` in Admin (`PlatformAuditConsumer`) and Notification (`DomainNotificationConsumer`).
- **Idempotency:** `processed_domain_events` table + `DurableEventGuard` prevents duplicate processing on Kafka retry.

See **[Platform Engineering](#platform-engineering-api--events)** above for full file paths and producer/consumer tables.

---

## Redis Caching

All cache names centralised in `FkCacheNames.java`:

| Cache Key | Used By | Default TTL |
|---|---|---|
| `farmer:id`, `farmer:userId` | Farmer Service | 30 min |
| `buyer:id`, `buyer:userId` | Buyer Service | 30 min |
| `product:id`, `product:list`, `product:categories` | Product Catalog | 30 min |
| `mandi:latest`, `mandi:history` | Market Price | 30 min |
| `warehouse:available` | Warehouse | 30 min |
| `session:user`, `otp:code`, `token:refresh` | Marketplace | 30 min |
| `framework:model`, `framework:view` | Framework | 30 min |

---

## Database Migrations

Conventions follow Farm Kart `microservices_boot` (epoch + JIRA naming). See **[MIGRATION.md](MIGRATION.md)** for full rules.

### Naming

| Engine | Pattern | Example |
|--------|---------|---------|
| MySQL / PostgreSQL (Flyway) | `V{epoch}__{JIRA}.sql` | `V1740787200__FARM-004.sql` |
| Cassandra (per service) | `{epoch}_{JIRA}.cql` | `1740787200_FARM-100.cql` |
| Elasticsearch index | `V{epoch}__{index}.json` | `V1719506000__farmkart_crops.json` |

Create a new script: `./scripts/create-migration.sh mysql farmer FARM-123 "description"`

### Databases & Flyway locations

All merged domains migrate into the **single `farmkart` MySQL database** via one Flyway
runner in `FarmKartAppConfig` (epoch-based versions are unique across domains, so the
histories merge cleanly). Migration SQL still lives per-domain in each `-repository` module.

| DB | Owner | Migration Paths |
|---|---|---|
| farmkart (MySQL) | Farm Kart App | `db/migration/{marketplace, farmer, buyer, logistics, warehouse, market_price, admin, product_catalog, reporting, ai_advisory}` |
| farmkart_framework (PostgreSQL) | Farm Kart App (framework datasource) | `db/migration/framework` |
| farmkart_warehouse_app (PostgreSQL) | Farm Kart App (warehouse-app datasource) | `db/migration/warehouse_app` |
| farmkart_notification (MySQL) | Notification Service | `db/migration/notification` |
| farmkart_agent (MySQL) | Agent Service | `db/migration/agent` |

Flyway settings (all services): `baseline-on-migrate`, `out-of-order`, `validate-on-migrate: false`.

### Other migration modules

| Module | Purpose |
|--------|---------|
| `microservice_boot/complex-migration-tracker/` | Multi-step MySQL + Cassandra + Elasticsearch migrations |
| `microservice_boot/elasticsearch-manager/` | Elasticsearch index templates (**not Solr**) |
| `microservice_boot/{service}/{service}-rest/cassandra/migration/` | Per-service Cassandra CQL scripts |

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 (Records, Pattern Matching, Text Blocks) |
| Framework | Spring Boot 3.3, Spring Security, Spring Data JPA |
| Build | Maven 3.9 — multi-module |
| Database | MySQL 8 + PostgreSQL 16 (framework) |
| Search | Elasticsearch 8.x (product catalog) |
| Event store | Cassandra (optional, per-service CQL migrations) |
| Cache / Sessions | Redis |
| Messaging | Apache Kafka (Confluent compatible) |
| Storage | AWS S3 |
| Migrations | Flyway (epoch + JIRA naming) |
| Security | JWT + OAuth2 + RBAC |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Frontend | React + Vite |

---

## CI/CD

The full enterprise pipeline (PR quality gates, SonarCloud, security scans,
Docker builds, dev/qa/stage/prod deployments with manual production approval,
auto-rollback and Email/Slack/Teams notifications) is documented in
**[docs/CICD.md](docs/CICD.md)**.

Quick facts:

- One required status check — **`Quality Gate`** (`.github/workflows/pr-checks.yml`) — aggregates build, tests, Checkstyle/PMD/SpotBugs, SonarCloud gate, Gitleaks, CodeQL, license compliance, OWASP and Docker builds. A red pipeline blocks the merge.
- Branch protection for `main`/`develop` (2 approvals, code owners, up-to-date branch, no force push): `./scripts/setup-branch-protection.sh`
- Images: `ghcr.io/<owner>/<repo>/{farm-kart-app, notification, agent}` tagged `sha-<sha>` (immutable), branch names and `latest`.
- Deployments: `deploy-dev` (auto from `develop`), `deploy-qa` (manual), `deploy-stage` (auto from `main`), `deploy-prod` (manual + required reviewers + typed confirmation), `rollback` (manual, any env).

---

## Local Development

### Prerequisites

```
Java 21+    Maven 3.9+    MySQL 8 (user: farmkart / pass: farmkart)
Docker      (recommended — Kafka + Kafka UI via ./scripts/kafka-infra.sh start)
Redis on localhost:6379
```

### Kafka + Kafka UI (Provectus)

Local dev ships a **KRaft Kafka broker** and **[Provectus Kafka UI](https://github.com/provectus/kafka-ui)** for topic/consumer-group inspection.

```bash
./scripts/kafka-infra.sh start          # Kafka :9092 + UI :8099
./scripts/kafka-infra.sh topics         # list farmkart.* topics
./scripts/kafka-infra.sh health         # broker + UI health
./scripts/kafka-infra.sh stop
```

| Component | URL / endpoint |
|---|---|
| **Kafka UI** | http://localhost:8099 |
| Bootstrap (host JVM services) | `localhost:9092` |
| Bootstrap (Docker network) | `kafka:29092` |

Topics are **pre-created** from `FkTopics.java` on first start (`infra/kafka/scripts/init-topics.sh`).

**Production:** Kafka UI connects to AWS MSK / secured cluster — see `infra/kafka/README.md` and `infra/kafka/k8s/`.

```bash
# Prod (UI only — brokers external)
cp infra/kafka/.env.example infra/kafka/.env   # fill SASL + UI password
./scripts/kafka-infra.sh start prod
```

`dev-local-startup.sh` auto-starts Kafka when Docker is available (`START_KAFKA=0` to skip).

### Start all services

```bash
./dev-local-startup.sh           # spring.profiles.active=dev (default)
SPRING_PROFILE=prod ./dev-local-startup.sh   # prod profile locally
./prod-local-startup.sh          # shorthand for prod profile
SKIP_BUILD=1 ./dev-local-startup.sh  # skip Maven build
./dev-local-stop.sh              # stop all
```

### Spring profiles (`dev` / `prod`)

Every microservice uses `application.yml` (base) + profile-specific overrides:

| File | Purpose |
|---|---|
| `application.yml` | Shared defaults (all environments) |
| `application-dev.yml` | Debug logging, Swagger enabled, console SMS, dev Kafka groups |
| `application-prod.yml` | Swagger off, stricter Flyway, larger pools, secrets required |
| `config/application-*-common.yml` | Shared profile rules in `starter-common` |

```bash
# Single service (JAR)
java -jar marketplace-rest.jar --spring.profiles.active=dev
java -jar marketplace-rest.jar --spring.profiles.active=prod

# Docker
SPRING_PROFILES_ACTIVE=prod docker compose up farm-kart-app
```

| Setting | `dev` | `prod` |
|---|---|---|
| Swagger / OpenAPI | Enabled | Disabled |
| Logging | `DEBUG` for `com.farmkart` | `WARN` root, `INFO` app |
| Flyway validate | Relaxed | Strict |
| JWT secret | Dev default | **`JWT_SECRET` required** |
| SMS | `console` | `fast2sms` (or env) |
| Kafka consumer groups | `*-dev` suffix | Production names |

### Swagger UIs

| Service | Swagger UI |
|---|---|
| Farm Kart App (all domains) | http://localhost:8080/farm-kart/swagger-ui.html |
| Notification | http://localhost:8087/notification-service/swagger-ui.html |
| Agent | http://localhost:8091/agent-service/swagger-ui.html |
| React UI | http://localhost:5173 |
| **Kafka UI (Provectus)** | http://localhost:8099 |

---

## Environment Variables

| Variable | Default | Used by |
|---|---|---|
| `SPRING_PROFILE` / `SPRING_PROFILES_ACTIVE` | `dev` (local) / `prod` (Docker default) | All services |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | All services |
| `KAFKA_UI_PORT` | `8099` | Kafka UI (dev/prod) |
| `KAFKA_UI_USERNAME` / `KAFKA_UI_PASSWORD` | — | Prod Kafka UI login |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` | All services |
| `DB_HOST` / `DB_PORT` | `localhost` / `3306` | dev-local-startup |
| `DB_USER` / `DB_PASS` | `farmkart` / `farmkart` | dev-local-startup |
| `AWS_REGION` | `ap-south-1` | Marketplace |
| `AWS_ACCESS_KEY_ID` | — | Marketplace |
| `AWS_SECRET_ACCESS_KEY` | — | Marketplace |
| `AWS_S3_BUCKET` | `farmkart-uploads` | Marketplace |

---

## Master Admin

The **Master Admin** role holds super-admin privileges and bypasses all role-based API restrictions across every microservice.

### Role hierarchy

| Role | Scope |
|---|---|
| `MASTER_ADMIN` | Full access to every API — no restrictions |
| `VENDOR` | Products, orders, vendors, warehouses |
| `DISTRIBUTOR` | Shipments, warehouses, catalog |
| `RESELLER` | Vendor + order + shipment paths |
| `BUYER` | Buyers, orders, catalog, market prices |
| `SELLER` | Farmers, vendors, warehouses, advisory |
| `CUSTOMER` | Buyer-facing paths (catalog, orders) |

### How the bypass works

```
Bearer JWT
  → JwtAuthenticationFilter  (parses role from token)
  → FkRoleAuthorizationManager
       │
       ├─ MASTER_ADMIN? → ALLOW immediately (all 7 acceptance criteria met)
       ├─ Public path?  → ALLOW (/api/v1/auth/*, /api/v1/currencies, Swagger)
       └─ Else          → check FkApiRoleRegistry (path → allowed roles)
```

### Register a Master Admin

```http
POST /marketplace/api/v1/auth/register
Content-Type: application/json

{
  "name": "Platform Admin",
  "email": "admin@farmkart.com",
  "mobile": "9999999999",
  "password": "strongPassword@123",
  "role": "MASTER_ADMIN"
}
```

### Use the Master Admin token

The token returned works on **every** endpoint (all domain APIs on :8080, plus notification/agent services):

```http
Authorization: Bearer <master_admin_token>

# Vendor API
GET /marketplace/api/v1/vendors

# Buyer API
GET /marketplace/api/v1/buyers/1

# Warehouse booking
POST /marketplace/api/v1/warehouses/smart-book

# Admin audit logs
GET /marketplace/api/v1/admin/audit-logs

# Farmer onboard
POST /marketplace/api/v1/farmers/onboard
```

### Implementation files

| Component | Module / path |
|---|---|
| `FkUserRoleEnum` (7 roles + MASTER_ADMIN bypass flag) | `starter-common/.../enums/` |
| `FkApiRoleRegistry` (path → allowed roles map) | `starter-common/.../security/` |
| `FkRoleAuthorizationManager` (Master Admin bypass logic) | `common-rest/.../security/` |
| `JwtAuthenticationFilter` (JWT → `FkUserPrincipal`) | `common-rest/.../security/` |
| `FkSecurityAutoConfiguration` (shared `SecurityFilterChain`) | `common-rest/.../security/` |
| `FkSecurityAutoConfiguration.imports` (auto-loads on all services) | `common-rest/resources/META-INF/spring/` |

> **Note:** The Farm Kart App, Notification and Agent services must share the same `JWT_SECRET` environment variable so the Master Admin token is accepted everywhere.

---

## Security

- JWT Authentication (HS512, configurable expiry)
- OAuth2 Social Login (Google)
- Role-Based Access Control — 7 roles (`MASTER_ADMIN`, `VENDOR`, `DISTRIBUTOR`, `RESELLER`, `BUYER`, `SELLER`, `CUSTOMER`)
- Master Admin bypasses all role restrictions (see [Master Admin](#master-admin) section)
- OTP-based phone verification
- Audit logging via Admin Service (REST + **Kafka event consumer**)
- **Safe API errors** — internal exceptions logged server-side only; clients receive structured `ApiError` codes

---

## Future Enhancements

- API Gateway (Spring Cloud Gateway or AWS API Gateway)
- Service Discovery (Consul / Eureka)
- Circuit Breaker (Resilience4j)
- Distributed Tracing (Zipkin / Jaeger)
- Kubernetes Helm charts
- Cassandra keyspace provisioning for catalog event tracking
- Flutter mobile app
- ML model integration for AI Advisory (Python/FastAPI sidecar)
- Blockchain supply chain tracking
