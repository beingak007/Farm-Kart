# Farm Kart — Kafka Operations (Provectus Kafka UI)

Operations guide for Apache Kafka and [Provectus Kafka UI](https://github.com/provectus/kafka-ui) across **dev** and **prod**.

## Architecture decision

| Environment | Kafka brokers | Kafka UI | Auth |
|---|---|---|---|
| **Dev** | Single-node KRaft (Docker) | `localhost:8099` | Disabled |
| **Prod** | AWS MSK / managed cluster | Internal ingress / VPN | LOGIN_FORM + read-only |

Microservices always connect via `KAFKA_BOOTSTRAP_SERVERS`. Kafka UI is **observability only** — not on the application request path.

## Dev — quick start

```bash
./scripts/kafka-infra.sh start
open http://localhost:8099
```

What starts:

1. `farmkart-kafka` — Apache Kafka 3.8 (KRaft, no Zookeeper)
2. `farmkart-kafka-init` — creates all `farmkart.*` topics from `FkTopics.java`
3. `farmkart-kafka-ui` — Provectus Kafka UI v0.7.2

## Dev — common tasks

| Task | Command / UI path |
|---|---|
| Browse topics | UI → Topics |
| Inspect consumer lag | UI → Consumer Groups → `farmkart-admin-audit`, etc. |
| View message payload | UI → Topics → `farmkart.order.created` → Messages |
| List topics (CLI) | `./scripts/kafka-infra.sh topics` |
| Reset dev stack | `./scripts/kafka-infra.sh stop && docker volume rm farmkart-kafka-dev_kafka_data` |

## Prod — Docker Compose (UI only)

Brokers run externally (MSK, Confluent Cloud, etc.). Only Kafka UI is deployed.

```bash
cd infra/kafka
cp .env.example .env
# Set KAFKA_BOOTSTRAP_SERVERS, SASL credentials, KAFKA_UI_PASSWORD
../../scripts/kafka-infra.sh start prod
```

Security defaults:

- Binds to `127.0.0.1` only (no public exposure)
- `AUTH_TYPE=LOGIN_FORM`
- `KAFKA_UI_READONLY=true` — no produce/delete from browser

## Prod — Kubernetes

```bash
kubectl apply -f infra/kafka/k8s/namespace.yaml
kubectl create secret generic kafka-ui-secret -n farmkart-kafka \
  --from-literal=KAFKA_UI_USERNAME=admin \
  --from-literal=KAFKA_UI_PASSWORD='...' \
  --from-literal=KAFKA_BOOTSTRAP_SERVERS='broker1:9092,broker2:9092' \
  --from-literal=KAFKA_SASL_JAAS_CONFIG='org.apache.kafka.common.security.scram.ScramLoginModule required username="..." password="...";'
kubectl apply -f infra/kafka/k8s/kafka-ui-deployment.yaml
kubectl apply -f infra/kafka/k8s/kafka-ui-service.yaml
kubectl apply -f infra/kafka/k8s/kafka-ui-ingress.yaml
kubectl apply -f infra/kafka/k8s/kafka-ui-networkpolicy.yaml
```

Adjust ingress host, TLS secret, and IP whitelist before production use.

## File layout

```
infra/kafka/
├── docker-compose.dev.yml      # Kafka + UI + topic init
├── docker-compose.prod.yml     # UI only (external cluster)
├── config/
│   ├── kafka-ui-dev.yaml
│   └── kafka-ui-prod.yaml
├── scripts/
│   ├── kafka-infra.sh
│   └── init-topics.sh
└── k8s/                        # Production manifests
```

## Security checklist (prod)

- [ ] Kafka UI not on public internet
- [ ] Strong UI password / SSO at ingress
- [ ] Read-only mode enabled
- [ ] SASL_SSL to brokers
- [ ] Secrets in K8s Secret / AWS Secrets Manager — never in git
- [ ] NetworkPolicy restricts egress to broker ports
