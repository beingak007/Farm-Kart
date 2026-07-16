# Farm Kart Database Migrations

Migration conventions follow the Yagna `microservices_boot` pattern, adapted for Farm Kart:
**Elasticsearch** for search (not Solr), **Flyway** for MySQL/PostgreSQL, **CQL scripts** for Cassandra.

## Naming conventions

All migration folders live under **`microservice_boot/`** (same root as the Java modules).

| Engine | Location (inside `microservice_boot/`) | File pattern | Example |
|--------|----------|--------------|---------|
| **MySQL** (per microservice) | `{service}-repository/src/main/resources/db/migration/{service_key}/` | `V{epoch_seconds}__{JIRA_ID}.sql` | `V1740787200__FARM-004.sql` |
| **PostgreSQL** (framework) | `framework-repository/.../db/migration/framework/` | `V{epoch_seconds}__{JIRA_ID}.sql` | `V1719504000__FARM-001.sql` |
| **Cassandra** (per microservice) | `{service}-rest/src/main/resources/cassandra/migration/` | `{epoch_seconds}_{JIRA_ID}.cql` | `1740787200_FARM-100.cql` |
| **Elasticsearch** (index mappings) | `elasticsearch-manager/src/main/resources/elasticsearch/migration/` | `V{epoch_seconds}__{index_name}.json` | `V1719506000__farmkart_crops.json` |
| **Complex** (multi-step) | `complex-migration-tracker/src/main/resources/migrations/` | folder `V{epoch_seconds}__{JIRA_ID}/` | `V1746519328__FARM-100/` |

### Rules

1. **Epoch at commit time** — run `date +%s` when creating a migration (avoids merge conflicts on version numbers).
2. **Flyway double underscore** — MySQL/PostgreSQL use `V{epoch}__{JIRA}.sql` (required by Flyway).
3. **Cassandra single underscore** — `{epoch}_{JIRA}.cql` (no `V` prefix).
4. **One DDL statement per file** for ALTER/CREATE/DROP when possible (especially Cassandra UDT changes).
5. Use `IF NOT EXISTS` / `IF EXISTS` for idempotent scripts.

## Create a new migration

```bash
# MySQL microservice (farmer, buyer, notification, …)
./scripts/create-migration.sh mysql farmer FARM-123 "add_kyc_document_column"

# PostgreSQL framework
./scripts/create-migration.sh postgres framework FARM-456 "add_grid_view_config"

# Cassandra (when keyspace is enabled for a service)
./scripts/create-migration.sh cassandra product-catalog FARM-789 "create_crop_search_events"
```

## Per-microservice layout

```
{service}/                              e.g. farmer/, marketplace/, framework/
├── {service}-repository/
│   └── src/main/resources/db/migration/{service_key}/
│       └── V{epoch}__FARM-xxx.sql          ← MySQL Flyway
└── {service}-rest/
    └── src/main/resources/
        ├── application.yml                  ← flyway.locations
        └── cassandra/migration/             ← optional CQL scripts
            └── {epoch}_FARM-xxx.cql
```

## Flyway configuration (all services)

```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    validate-on-migrate: false
    out-of-order: true
    locations: classpath:db/migration/{service_key}
```

Programmatic Flyway (marketplace + framework) uses `FlywayMigrationDefaults` in `starter-common`.

## Complex migrations

For changes spanning MySQL + Cassandra + Elasticsearch + ETL, use
`microservice_boot/complex-migration-tracker/`:

```
V{epoch}__FARM-xxx/
├── order          # execution order (one filename per line)
├── keyspace       # KEYSPACE=... DATABASE=...
├── 1.sql
├── 2.cql
├── 3.json         # Elasticsearch index mapping
└── 4.yml          # optional ETL spec
```

Validate before commit:

```bash
cd microservice_boot/complex-migration-tracker && ./validate.sh
```

## Elasticsearch (not Solr)

Index mappings live in `microservice_boot/elasticsearch-manager`. The product catalog service reads
`ELASTICSEARCH_HOST` and uses indices created by the ES migration runner.

**Do not use Solr** — Farm Kart standardises on Elasticsearch 8.x for full-text crop/product search.

## Reference

Based on: `/home/akashs/Documents/YagnaCodeBase/yagna/microservices_boot`
- `db_migration_Mysql` → Farm Kart per-service Flyway
- `db_migration_Cassandra` → `{service}-rest/cassandra/migration`
- `complex-migration-tracker` → `complex-migration-tracker`
- `cassandra-solr-core-manager` → **`elasticsearch-manager`** (Elasticsearch replaces Solr)
