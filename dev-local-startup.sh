#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Farm Kart — local startup
# Starts the merged Farm Kart app, notification service, agent service and UI,
# each in its own screen session.
# Prerequisites: Java 17+, Maven 3.9+, MySQL running
# Kafka: run ./scripts/kafka-infra.sh start  (or external broker on :9092)
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

# ── Kafka (optional auto-start) ─────────────────────────────────────────────
if [[ "${START_KAFKA:-1}" == "1" ]]; then
    if ! command -v docker >/dev/null 2>&1; then
        echo "WARN: Docker not found — start Kafka manually or set KAFKA_BOOTSTRAP_SERVERS"
    elif ! docker ps --format '{{.Names}}' 2>/dev/null | grep -q '^farmkart-kafka$'; then
        echo "Starting Kafka dev stack (Kafka + Provectus Kafka UI)…"
        "$ROOT/scripts/kafka-infra.sh" start dev || echo "WARN: Kafka infra failed — microservices may not publish events"
    fi
fi

# ── Shared env vars ───────────────────────────────────────────────────────────
export SPRING_PROFILE="${SPRING_PROFILE:-dev}"
export PROFILE_OPTS="-Dspring.profiles.active=${SPRING_PROFILE}"
export KAFKA_BOOTSTRAP_SERVERS="${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}"
export DB_HOST="${DB_HOST:-localhost}"
export DB_PORT="${DB_PORT:-3306}"
export DB_USER="${DB_USER:-farmkart}"
export DB_PASS="${DB_PASS:-farmkart}"

# AWS / S3 (used by marketplace service)
export AWS_REGION="${AWS_REGION:-ap-south-1}"
export AWS_ACCESS_KEY_ID="${AWS_ACCESS_KEY_ID:-}"
export AWS_SECRET_ACCESS_KEY="${AWS_SECRET_ACCESS_KEY:-}"
export AWS_S3_BUCKET="${AWS_S3_BUCKET:-farmkart-uploads}"

# ── Helper ────────────────────────────────────────────────────────────────────
start_service() {
    local SESSION="$1"
    local JAR="$2"
    local EXTRA_OPTS="${3:-}"

    if screen -list | grep -q "$SESSION"; then
        echo "[$SESSION] already running — skip"
        return
    fi

    echo "Starting [$SESSION] → $JAR"
    screen -dmS "$SESSION" bash -c "java -jar $EXTRA_OPTS '$JAR'; exec bash"
    sleep 1
}

# ── Build (skip if SKIP_BUILD=1) ─────────────────────────────────────────────
if [[ "${SKIP_BUILD:-0}" != "1" ]]; then
    echo "Building all modules…"
    cd "$ROOT"
    mvn clean package -DskipTests -q
fi

# ── Service port map ──────────────────────────────────────────────────────────
#   farm-kart app   :8080  (marketplace + farmer + buyer + logistics + warehouse
#                           + market-price + admin + catalog + reporting + ai-advisory)
#   notification    :8087
#   agent           :8091

# ── Start services (profile: ${SPRING_PROFILE}) ───────────────────────────────
start_service "fk-app" \
    "$ROOT/farm-kart/farm-kart-rest/target/farm-kart-rest-*.jar" \
    "${PROFILE_OPTS} -DMYSQL_DATASOURCE_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DMYSQL_DATASOURCE_USERNAME=$DB_USER -DMYSQL_DATASOURCE_PASSWORD=$DB_PASS"

start_service "fk-notification" \
    "$ROOT/farm-kart-notification/farm-kart-notification-rest/target/farm-kart-notification-rest-*.jar" \
    "${PROFILE_OPTS} -DNOTIF_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_notification?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DNOTIF_DB_USER=$DB_USER -DNOTIF_DB_PASS=$DB_PASS"

start_service "fk-agent" \
    "$ROOT/farm-kart-agent/farm-kart-agent-rest/target/farm-kart-agent-rest-*.jar" \
    "${PROFILE_OPTS} -DAGENT_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_agent?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DAGENT_DB_USER=$DB_USER -DAGENT_DB_PASS=$DB_PASS -DOPENAI_API_KEY=${OPENAI_API_KEY:-}"

# ── UI ────────────────────────────────────────────────────────────────────────
if [[ -d "$ROOT/farm-kart-ui" ]]; then
    if ! screen -list | grep -q "fk-ui"; then
        echo "Starting [fk-ui] → npm dev"
        screen -dmS "fk-ui" bash -c "cd '$ROOT/farm-kart-ui' && npm run dev; exec bash"
    fi
fi

echo ""
echo "✓ All services started (spring.profiles.active=${SPRING_PROFILE}). Attach with: screen -r <session>"
echo ""
echo "  Session          URL"
echo "  ─────────────    ──────────────────────────────────────────"
echo "  fk-app           http://localhost:8080/farm-kart/swagger-ui.html"
echo "  fk-notification  http://localhost:8087/notification-service/swagger-ui.html"
echo "  fk-agent         http://localhost:8091/agent-service/swagger-ui.html"
echo "  fk-ui            http://localhost:5173"
echo "  fk-kafka-ui      http://localhost:8099  (Provectus Kafka UI)"
echo ""
echo "List all: screen -ls"
echo "Kafka ops: ./scripts/kafka-infra.sh {start|stop|status|topics|health}"
