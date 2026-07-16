# Complex Migration Tracker

Orchestrates multi-step migrations across **MySQL**, **PostgreSQL**, **Cassandra**, and **Elasticsearch**.

Modelled on Yagna `complex-migration-tracker`; Farm Kart uses **Elasticsearch** instead of Solr.

## Folder structure

```
src/main/resources/migrations/V{epoch_seconds}__{JIRA_ID}/
├── order       # mandatory — one script name per line
├── keyspace    # mandatory — KEYSPACE=... DATABASE=...
├── 1.sql       # MySQL or PostgreSQL
├── 2.cql       # Cassandra
├── 3.json      # Elasticsearch index mapping
└── 4.yml       # optional Spark ETL job
```

## File naming inside a migration folder

- Scripts: `{integer}.{ext}` — e.g. `1.sql`, `2.cql`, `3.json`
- **Never** use `4_1`, `4_2` style names
- One CREATE or DROP per `.sql` / `.cql` file

## Prerequisites

Run tracker DDL once per environment:

- MySQL: `COMPLEX_MIGRATION_TRACKER.sql`
- Cassandra: `COMPLEX_MIGRATION_TRACKER.cql`

## Validate locally

```bash
./validate.sh
```

## Example

See `src/main/resources/migrations/V1746519328__FARM-100/` for a sample MySQL + Elasticsearch crop reindex migration.
