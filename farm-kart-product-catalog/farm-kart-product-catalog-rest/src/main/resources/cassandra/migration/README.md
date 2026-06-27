# Cassandra migrations — product-catalog service

Per-microservice CQL scripts. Naming: `{epoch_seconds}_{JIRA_ID}.cql`

## Configuration (when Cassandra is enabled)

```yaml
spring:
  cassandra:
    contact-points: ${CASSANDRA_IP:localhost}
    port: 9042
    keyspace-name: ${CATALOG_CASSANDRA_KEYSPACE:farmkart_catalog}
    local-datacenter: ${CASSANDRA_DC:datacenter1}

cassandra:
  migration:
    keyspace-name: ${CATALOG_CASSANDRA_KEYSPACE:farmkart_catalog}
    script-location: /cassandra/migration
    strategy: IGNORE_DUPLICATES
```

## Best practices (from Yagna)

1. One ALTER UDT per file
2. Do not mix CREATE and DROP in one file
3. Use `IF NOT EXISTS` / `IF EXISTS`
4. Epoch + JIRA filename at commit time: `1740787200_FARM-789.cql`

Place scripts in this directory.
