#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Farm Kart — multi-service local startup
# Starts every microservice in its own screen session.
# Prerequisites: Java 17+, Maven 3.9+, MySQL running, Kafka running on :9092
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"

# ── Shared env vars ───────────────────────────────────────────────────────────
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
#   marketplace     :8080  (farm-kart — existing core service)
#   farmer          :8081
#   buyer           :8082
#   logistics       :8083
#   warehouse       :8084
#   market-price    :8085
#   admin           :8086
#   notification    :8087
#   product-catalog :8088
#   reporting       :8089
#   ai-advisory     :8090

# ── Start services ────────────────────────────────────────────────────────────
start_service "fk-marketplace" \
    "$ROOT/farm-kart/farm-kart-rest/target/farm-kart-rest-*.jar" \
    "-Dspring.profiles.active=dev"

start_service "fk-farmer" \
    "$ROOT/farm-kart-farmer/farm-kart-farmer-rest/target/farm-kart-farmer-rest-*.jar" \
    "-DFARMER_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_farmer?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DFARMER_DB_USER=$DB_USER -DFARMER_DB_PASS=$DB_PASS"

start_service "fk-buyer" \
    "$ROOT/farm-kart-buyer/farm-kart-buyer-rest/target/farm-kart-buyer-rest-*.jar" \
    "-DBUYER_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_buyer?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DBUYER_DB_USER=$DB_USER -DBUYER_DB_PASS=$DB_PASS"

start_service "fk-logistics" \
    "$ROOT/farm-kart-logistics/farm-kart-logistics-rest/target/farm-kart-logistics-rest-*.jar" \
    "-DLOGISTICS_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_logistics?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DLOGISTICS_DB_USER=$DB_USER -DLOGISTICS_DB_PASS=$DB_PASS"

start_service "fk-warehouse" \
    "$ROOT/farm-kart-warehouse/farm-kart-warehouse-rest/target/farm-kart-warehouse-rest-*.jar" \
    "-DWAREHOUSE_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_warehouse?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DWAREHOUSE_DB_USER=$DB_USER -DWAREHOUSE_DB_PASS=$DB_PASS"

start_service "fk-market-price" \
    "$ROOT/farm-kart-market-price/farm-kart-market-price-rest/target/farm-kart-market-price-rest-*.jar" \
    "-DMARKET_PRICE_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_market_price?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DMARKET_PRICE_DB_USER=$DB_USER -DMARKET_PRICE_DB_PASS=$DB_PASS"

start_service "fk-admin" \
    "$ROOT/farm-kart-admin/farm-kart-admin-rest/target/farm-kart-admin-rest-*.jar" \
    "-DADMIN_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_admin?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DADMIN_DB_USER=$DB_USER -DADMIN_DB_PASS=$DB_PASS"

start_service "fk-notification" \
    "$ROOT/farm-kart-notification/farm-kart-notification-rest/target/farm-kart-notification-rest-*.jar" \
    "-DNOTIF_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_notification?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DNOTIF_DB_USER=$DB_USER -DNOTIF_DB_PASS=$DB_PASS"

start_service "fk-catalog" \
    "$ROOT/farm-kart-product-catalog/farm-kart-product-catalog-rest/target/farm-kart-product-catalog-rest-*.jar" \
    "-DCATALOG_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_product_catalog?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DCATALOG_DB_USER=$DB_USER -DCATALOG_DB_PASS=$DB_PASS"

start_service "fk-reporting" \
    "$ROOT/farm-kart-reporting/farm-kart-reporting-rest/target/farm-kart-reporting-rest-*.jar" \
    "-DREPORTING_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_reporting?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DREPORTING_DB_USER=$DB_USER -DREPORTING_DB_PASS=$DB_PASS"

start_service "fk-ai-advisory" \
    "$ROOT/farm-kart-ai-advisory/farm-kart-ai-advisory-rest/target/farm-kart-ai-advisory-rest-*.jar" \
    "-DAI_ADVISORY_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_ai_advisory?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DAI_ADVISORY_DB_USER=$DB_USER -DAI_ADVISORY_DB_PASS=$DB_PASS"

start_service "fk-agent" \
    "$ROOT/farm-kart-agent/farm-kart-agent-rest/target/farm-kart-agent-rest-*.jar" \
    "-DAGENT_DB_URL=jdbc:mysql://$DB_HOST:$DB_PORT/farmkart_agent?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true -DAGENT_DB_USER=$DB_USER -DAGENT_DB_PASS=$DB_PASS -DOPENAI_API_KEY=${OPENAI_API_KEY:-}"

# ── UI ────────────────────────────────────────────────────────────────────────
if [[ -d "$ROOT/farm-kart-ui" ]]; then
    if ! screen -list | grep -q "fk-ui"; then
        echo "Starting [fk-ui] → npm dev"
        screen -dmS "fk-ui" bash -c "cd '$ROOT/farm-kart-ui' && npm run dev; exec bash"
    fi
fi

echo ""
echo "✓ All services started. Attach with: screen -r <session>"
echo ""
echo "  Session          URL"
echo "  ─────────────    ──────────────────────────────────────────"
echo "  fk-marketplace   http://localhost:8080/swagger-ui.html"
echo "  fk-farmer        http://localhost:8081/farmer-service/swagger-ui.html"
echo "  fk-buyer         http://localhost:8082/buyer-service/swagger-ui.html"
echo "  fk-logistics     http://localhost:8083/logistics-service/swagger-ui.html"
echo "  fk-warehouse     http://localhost:8084/warehouse-service/swagger-ui.html"
echo "  fk-market-price  http://localhost:8085/market-price-service/swagger-ui.html"
echo "  fk-admin         http://localhost:8086/admin-service/swagger-ui.html"
echo "  fk-notification  http://localhost:8087/notification-service/swagger-ui.html"
echo "  fk-catalog       http://localhost:8088/catalog-service/swagger-ui.html"
echo "  fk-reporting     http://localhost:8089/reporting-service/swagger-ui.html"
echo "  fk-ai-advisory   http://localhost:8090/ai-advisory-service/swagger-ui.html"
echo "  fk-agent         http://localhost:8091/agent-service/swagger-ui.html"
echo "  fk-ui            http://localhost:5173"
echo ""
echo "List all: screen -ls"
