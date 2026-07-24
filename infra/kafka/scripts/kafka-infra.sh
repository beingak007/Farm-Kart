#!/usr/bin/env bash
# Farm Kart — Kafka infrastructure helper (dev + prod)
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
COMPOSE_DEV="${ROOT}/docker-compose.dev.yml"
COMPOSE_PROD="${ROOT}/docker-compose.prod.yml"
ENV_FILE="${ROOT}/.env"

usage() {
    cat <<EOF
Farm Kart Kafka Infrastructure

Usage: $(basename "$0") <command> [environment]

Commands:
  start [dev|prod]   Start Kafka stack (default: dev)
  stop [dev|prod]    Stop stack
  restart [dev|prod] Restart stack
  status [dev|prod]  Show container status
  logs [dev|prod]    Tail logs (kafka-ui by default)
  topics             List topics (dev only)
  health             Check broker + UI reachability

Examples:
  $(basename "$0") start              # dev: Kafka + Kafka UI on :8099
  $(basename "$0") start prod         # prod: Kafka UI only (needs .env)
  $(basename "$0") logs dev kafka-ui

URLs (dev):
  Kafka UI:  http://localhost:8099
  Bootstrap: localhost:9092
EOF
}

compose_file() {
    local env="${1:-dev}"
    case "$env" in
        dev)  echo "$COMPOSE_DEV" ;;
        prod) echo "$COMPOSE_PROD" ;;
        *)    echo "Unknown environment: $env" >&2; exit 1 ;;
    esac
}

compose_cmd() {
    local env="${1:-dev}"
    local file
    file="$(compose_file "$env")"
    if [[ "$env" == "prod" && -f "$ENV_FILE" ]]; then
        docker compose -f "$file" --env-file "$ENV_FILE" "${@:2}"
    else
        docker compose -f "$file" "${@:2}"
    fi
}

cmd_start() {
    local env="${1:-dev}"
    if [[ "$env" == "prod" && ! -f "$ENV_FILE" ]]; then
        echo "ERROR: Copy infra/kafka/.env.example → infra/kafka/.env and set prod credentials." >&2
        exit 1
    fi
    echo "Starting Kafka infrastructure ($env)…"
    compose_cmd "$env" up -d
    if [[ "$env" == "dev" ]]; then
        echo ""
        echo "✓ Dev Kafka ready"
        echo "  Kafka UI:  http://localhost:${KAFKA_UI_PORT:-8099}"
        echo "  Bootstrap: localhost:9092"
        echo "  Topics:    pre-created from FkTopics.java"
    else
        echo ""
        echo "✓ Prod Kafka UI ready (localhost-only bind)"
        echo "  Kafka UI: http://127.0.0.1:${KAFKA_UI_PORT:-8099}"
    fi
}

cmd_stop() {
    local env="${1:-dev}"
    compose_cmd "$env" down
    echo "Stopped ($env)."
}

cmd_restart() {
    cmd_stop "${1:-dev}"
    cmd_start "${1:-dev}"
}

cmd_status() {
    local env="${1:-dev}"
    compose_cmd "$env" ps
}

cmd_logs() {
    local env="${1:-dev}"
    local service="${2:-kafka-ui}"
    compose_cmd "$env" logs -f "$service"
}

cmd_topics() {
    docker exec farmkart-kafka /opt/kafka/bin/kafka-topics.sh \
        --bootstrap-server kafka:29092 --list 2>/dev/null \
        || echo "Dev Kafka not running. Run: $(basename "$0") start"
}

cmd_health() {
    local ok=0
    if docker exec farmkart-kafka /opt/kafka/bin/kafka-broker-api-versions.sh \
        --bootstrap-server localhost:9092 >/dev/null 2>&1; then
        echo "✓ Kafka broker (localhost:9092)"
    else
        echo "✗ Kafka broker unreachable"
        ok=1
    fi
    if curl -sf "http://localhost:${KAFKA_UI_PORT:-8099}/actuator/health" | grep -q UP; then
        echo "✓ Kafka UI (http://localhost:${KAFKA_UI_PORT:-8099})"
    else
        echo "✗ Kafka UI unreachable"
        ok=1
    fi
    exit "$ok"
}

# Load optional .env for port overrides
[[ -f "$ENV_FILE" ]] && set -a && source "$ENV_FILE" && set +a

CMD="${1:-}"
ENV="${2:-dev}"
case "$CMD" in
    start)   cmd_start "$ENV" ;;
    stop)    cmd_stop "$ENV" ;;
    restart) cmd_restart "$ENV" ;;
    status)  cmd_status "$ENV" ;;
    logs)    cmd_logs "$ENV" "${3:-kafka-ui}" ;;
    topics)  cmd_topics ;;
    health)  cmd_health ;;
    -h|--help|help|"") usage ;;
    *) echo "Unknown command: $CMD" >&2; usage; exit 1 ;;
esac
