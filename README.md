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

A production-ready multi-module microservices platform (Java 21 + Spring Boot 3.3.5) connecting farmers directly with buyers, logistics partners, warehouses, and financial institutions.

---

## Platform Engineering (API + Events)

Recent senior-architecture changes applied across all microservices. Source modules are listed so you know **where** each piece lives.

### Standard API envelope (no internal errors exposed)

All REST services return the same JSON contract. Internal stack traces, SQL, and provider errors never reach the client.

| Component | Module / path |
|---|---|
| `ApiResponse`, `ApiError`, `ApiErrorCode`, `FieldErrorDetail`, `ResponseMeta` | `farm-kart-starter-common/.../dto/` |
| `BusinessException` (safe user-facing messages + error codes) | `farm-kart-starter-common/.../exception/` |
| `RequestContext` (correlation ID via MDC) | `farm-kart-starter-common/.../context/` |
| `RequestIdFilter` (`X-Request-Id` on every request/response) | `farm-kart-common-rest/.../filter/` |
| `GlobalExceptionHandler` (validation, 404, 409, 500 — safe messages only) | `farm-kart-common-rest/.../exception/` |
| `ApiResponseBodyAdvice` (adds `meta.requestId` + `meta.timestamp` on success) | `farm-kart-common-rest/.../advice/` |
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
| `FkTopics`, `FkBaseEvent`, domain event records | `farm-kart-starter-common/.../events/` |
| `DomainEventPublisher` (transaction-aware publish) | `farm-kart-starter-common/.../events/` |
| `FkKafkaAutoConfiguration`, `EventProcessingGuard` | `farm-kart-starter-common/.../configuration/` |
| `FkKafkaConsumerConfiguration` (`@EnableKafka`) | `farm-kart-starter-common/.../events/` |
| `PlatformAuditConsumer` (writes audit logs from domain events) | `farm-kart-admin/.../service/` |
| `DomainNotificationConsumer` (welcome, order, payment, delivery alerts) | `farm-kart-notification/.../service/` |
| `DurableEventGuard` + `processed_domain_events` table (idempotency) | `farm-kart-admin`, `farm-kart-notification` |

**Event flow example (farmer onboard):**

```
POST /farmer-service/onboard
  → DB save
  → Kafka: farmkart.farmer.created
  → Admin service: audit log
  → Notification service: welcome PUSH/SMS
```

**Producers (who publishes what):**

| Service | Topics published |
|---|---|
| Marketplace (8080) | `user.registered`, `order.created`, `order.cancelled`, `payment.success`, `payment.failed`, `sheet.uploaded` |
| Farmer (8081) | `farmer.created`, `farmer.verified` |
| Buyer (8082) | `buyer.created` |
| Logistics (8083) | `shipment.created`, `shipment.delivered` |
| Warehouse (8084) | `warehouse.booked` |
| Market Price (8085) | `market.price.updated` |
| Product Catalog (8088) | `crop.listed` |

**Consumers:**

| Service | Listens to | Action |
|---|---|---|
| Admin (8086) | All major domain topics | Persist `audit_logs` |
| Notification (8087) | `user.registered`, `farmer.*`, `order.created`, `payment.success`, `shipment.delivered` | SMS / Push / Email |
| Notification (8087) | `notification.triggered` | Template-based dispatch (existing) |

**Developer rule:** use `DomainEventPublisher.publish(topic, key, event)` in services — not raw `KafkaTemplate.send()`.

---

## Architecture Overview

```
  React UI  ─────────────────────────────────── port 5173
       │
       ▼
  API Gateway (future)
       │
  ┌────┴────────────────────────────────────────────────────┐
  │                  Microservices Layer                     │
  │                                                          │
  │  Marketplace    :8080   Farmer      :8081               │
  │  Buyer          :8082   Logistics   :8083               │
  │  Warehouse      :8084   MarketPrice :8085               │
  │  Admin          :8086   Notification:8087               │
  │  ProductCatalog :8088   Reporting   :8089               │
  │  AI Advisory    :8090                                    │
  └──────────────────────────┬──────────────────────────────┘
                             │ Kafka (Event Bus)
  ┌──────────────────────────┴──────────────────────────────┐
  │                  Data Layer                              │
  │  MySQL (per-service DB)   Redis (cache/sessions)        │
  │  Kafka Cluster            AWS S3 (media/files)          │
  └─────────────────────────────────────────────────────────┘
```

---

## Project Structure

```
farm-kart-parent/                         ← Root Maven POM (Java 21)
│
├── farm-kart-starter-common/             ← Shared foundation
│   ├── ApiResponse, ApiError, ApiErrorCode, BusinessException
│   ├── DomainEventPublisher, FkKafkaAutoConfiguration
│   ├── FkTopics.java                     All Kafka topic constants
│   ├── FkCacheNames.java                 All Redis cache key constants
│   ├── RedisConfig.java                  Shared Redis/cache configuration
│   └── Domain events (FarmerCreatedEvent, OrderCreatedEvent, UserRegisteredEvent, etc.)
│
├── farm-kart-common-rest/                ← REST config, exception handling, request tracing
│   ├── GlobalExceptionHandler            Safe API errors (no internal leaks)
│   ├── RequestIdFilter                   X-Request-Id correlation
│   └── ApiResponseBodyAdvice             meta on every response
├── farm-kart-framework/                  ← Dynamic view/model metadata engine
│
├── farm-kart/                            ← Marketplace Service     (port 8080)
├── farm-kart-farmer/                     ← Farmer Service          (port 8081)
├── farm-kart-buyer/                      ← Buyer Service           (port 8082)
├── farm-kart-logistics/                  ← Logistics Service       (port 8083)
├── farm-kart-warehouse/                  ← Warehouse Service       (port 8084)
├── farm-kart-market-price/               ← Market Price Service    (port 8085)
├── farm-kart-admin/                      ← Admin Service           (port 8086)
├── farm-kart-notification/               ← Notification Service    (port 8087)
├── farm-kart-product-catalog/            ← Product Catalog Service (port 8088)
├── farm-kart-reporting/                  ← Reporting Service       (port 8089)
├── farm-kart-ai-advisory/                ← AI Advisory Service     (port 8090)
│
└── farm-kart-ui/                         ← React frontend          (port 5173)
```

Each microservice is a **4-layer Maven sub-project**:
```
farm-kart-<service>/
├── farm-kart-<service>-client/      DTOs, enums, request/response contracts
├── farm-kart-<service>-repository/  JPA entities, repositories, Flyway migrations
├── farm-kart-<service>-service/     Business logic, Kafka producers/consumers
└── farm-kart-<service>-rest/        Spring Boot Application, controllers, application.yml
```

---

## Services & Responsibilities

| Service | Port | DB | Key Features |
|---|---|---|---|
| **Marketplace** | 8080 | farmkart_marketplace | Auth (JWT/OAuth2), Orders, Payments, S3 uploads, **publishes user/order/payment/sheet events** |
| **Farmer** | 8081 | farmkart_farmer | Farmer onboarding, farm details, verification, state/district search |
| **Buyer** | 8082 | farmkart_buyer | Buyer onboarding (Individual/Retailer/Exporter), profile management |
| **Logistics** | 8083 | farmkart_logistics | Shipment creation, tracking number, status updates |
| **Warehouse** | 8084 | farmkart_warehouse | Warehouse listing, cold storage, booking with auto cost calc |
| **Market Price** | 8085 | farmkart_market_price | Mandi rate ingestion, latest prices, historical analytics |
| **Admin** | 8086 | farmkart_admin | Audit log storage, **Kafka audit consumer** (`PlatformAuditConsumer`), query by user/service/resource |
| **Notification** | 8087 | farmkart_notification | SMS/Email/Push/WhatsApp, **domain event consumer** (`DomainNotificationConsumer`), template-based |
| **Product Catalog** | 8088 | farmkart_product_catalog | Crop categories, crop CRUD, full-text search, organic filter |
| **Reporting** | 8089 | farmkart_reporting | Async report jobs (SALES, ORDER_SUMMARY, FARMER_ACTIVITY, etc.) |
| **AI Advisory** | 8090 | farmkart_ai_advisory | Crop recommendations by season/soil, demand & price forecasting |

---

## Event-Driven Architecture (Kafka)

All topics are centralised in `FkTopics.java` (`farm-kart-starter-common`).

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

- **Publish:** `DomainEventPublisher` in `farm-kart-starter-common` — called from service layer after DB save.
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

Conventions follow Yagna `microservices_boot` (epoch + JIRA naming). See **[MIGRATION.md](MIGRATION.md)** for full rules.

### Naming

| Engine | Pattern | Example |
|--------|---------|---------|
| MySQL / PostgreSQL (Flyway) | `V{epoch}__{JIRA}.sql` | `V1740787200__FARM-004.sql` |
| Cassandra (per service) | `{epoch}_{JIRA}.cql` | `1740787200_FARM-100.cql` |
| Elasticsearch index | `V{epoch}__{index}.json` | `V1719506000__farmkart_crops.json` |

Create a new script: `./scripts/create-migration.sh mysql farmer FARM-123 "description"`

### Per-microservice MySQL (Flyway)

| Service | DB Name | Migration Path |
|---|---|---|
| Marketplace | farmkart | `db/migration/marketplace` |
| Framework | farmkart_framework (PostgreSQL) | `db/migration/framework` |
| Farmer | farmkart_farmer | `db/migration/farmer` |
| Buyer | farmkart_buyer | `db/migration/buyer` |
| Logistics | farmkart_logistics | `db/migration/logistics` |
| Warehouse | farmkart_warehouse | `db/migration/warehouse` |
| Market Price | farmkart_market_price | `db/migration/market_price` |
| Admin | farmkart_admin | `db/migration/admin` |
| Notification | farmkart_notification | `db/migration/notification` |
| Product Catalog | farmkart_product_catalog | `db/migration/product_catalog` |
| Reporting | farmkart_reporting | `db/migration/reporting` |
| AI Advisory | farmkart_ai_advisory | `db/migration/ai_advisory` |
| Agent | farmkart_agent | `db/migration/agent` |

Flyway settings (all services): `baseline-on-migrate`, `out-of-order`, `validate-on-migrate: false`.

### Other migration modules

| Module | Purpose |
|--------|---------|
| `farm-kart-complex-migration-tracker/` | Multi-step MySQL + Cassandra + Elasticsearch migrations |
| `farm-kart-elasticsearch-manager/` | Elasticsearch index templates (**not Solr**) |
| `{service}-rest/cassandra/migration/` | Per-service Cassandra CQL scripts |

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

## Local Development

### Prerequisites

```
Java 21+    Maven 3.9+    MySQL 8 (user: farmkart / pass: farmkart)
Kafka on localhost:9092    Redis on localhost:6379
```

### Start all services

```bash
./dev-local-startup.sh           # build + start everything
SKIP_BUILD=1 ./dev-local-startup.sh  # skip Maven build
./dev-local-stop.sh              # stop all
```

### Swagger UIs

| Service | Swagger UI |
|---|---|
| Marketplace | http://localhost:8080/swagger-ui.html |
| Farmer | http://localhost:8081/farmer-service/swagger-ui.html |
| Buyer | http://localhost:8082/buyer-service/swagger-ui.html |
| Logistics | http://localhost:8083/logistics-service/swagger-ui.html |
| Warehouse | http://localhost:8084/warehouse-service/swagger-ui.html |
| Market Price | http://localhost:8085/market-price-service/swagger-ui.html |
| Admin | http://localhost:8086/admin-service/swagger-ui.html |
| Notification | http://localhost:8087/notification-service/swagger-ui.html |
| Product Catalog | http://localhost:8088/catalog-service/swagger-ui.html |
| Reporting | http://localhost:8089/reporting-service/swagger-ui.html |
| AI Advisory | http://localhost:8090/ai-advisory-service/swagger-ui.html |
| React UI | http://localhost:5173 |

---

## Environment Variables

| Variable | Default | Used by |
|---|---|---|
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | All services |
| `REDIS_HOST` / `REDIS_PORT` | `localhost` / `6379` | All services |
| `DB_HOST` / `DB_PORT` | `localhost` / `3306` | dev-local-startup |
| `DB_USER` / `DB_PASS` | `farmkart` / `farmkart` | dev-local-startup |
| `AWS_REGION` | `ap-south-1` | Marketplace |
| `AWS_ACCESS_KEY_ID` | — | Marketplace |
| `AWS_SECRET_ACCESS_KEY` | — | Marketplace |
| `AWS_S3_BUCKET` | `farmkart-uploads` | Marketplace |

---

## Security

- JWT Authentication (HS512, configurable expiry)
- OAuth2 Social Login (Google)
- Role-Based Access Control (FARMER / BUYER / LOGISTICS_PARTNER / WAREHOUSE_PARTNER / REGIONAL_ADMIN / MASTER_ADMIN)
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
