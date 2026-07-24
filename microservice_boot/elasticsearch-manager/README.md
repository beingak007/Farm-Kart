# Elasticsearch Index Migration Manager

Farm Kart uses **Elasticsearch 8.x** for product/crop full-text search.
This replaces the previous Cassandra-Solr approach — **do not use Solr**.

## Layout

```
src/main/resources/elasticsearch/migration/
└── {service}/
    └── {index}/
        └── V{epoch_seconds}__{index_name}.json
```

## Naming

| Pattern | Example |
|---------|---------|
| Index mapping file | `V1719506000__farmkart_crops.json` |
| Tracker table | `elasticsearch_index_tracker` (MySQL) |

## Apply mappings

Mappings are applied by the ES migration runner (or manually via `_index_template` API):

```bash
curl -X PUT "http://localhost:9200/_index_template/farmkart_crops" \
  -H "Content-Type: application/json" \
  -d @src/main/resources/elasticsearch/migration/product-catalog/crops/V1719506000__farmkart_crops.json
```

## Environment variables

| Variable | Default |
|----------|---------|
| `ELASTICSEARCH_HOST` | `http://localhost:9200` |
| `ELASTICSEARCH_USERNAME` | — |
| `ELASTICSEARCH_PASSWORD` | — |

Used by `product-catalog` for crop search APIs.
