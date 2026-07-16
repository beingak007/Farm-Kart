#!/usr/bin/env bash
# Create a new migration file using epoch + JIRA id naming (Yagna-style).
#
# Usage:
#   ./scripts/create-migration.sh mysql  farmer      FARM-123 "add_kyc_column"
#   ./scripts/create-migration.sh postgres framework  FARM-456 "add_view_config"
#   ./scripts/create-migration.sh cassandra product_catalog FARM-789 "create_crop_events"
#
set -euo pipefail

DB_TYPE="${1:?db type required: mysql|postgres|cassandra}"
SERVICE="${2:?service key required e.g. farmer, marketplace, framework}"
JIRA="${3:?JIRA id required e.g. FARM-123}"
DESC="${4:-migration}"

EPOCH="$(date +%s)"
# All Java modules live under microservice_boot/
ROOT="$(cd "$(dirname "$0")/.." && pwd)/microservice_boot"

case "$DB_TYPE" in
  mysql|postgres)
    EXT="sql"
    if [[ "$SERVICE" == "framework" ]]; then
      DIR="$ROOT/framework/framework-repository/src/main/resources/db/migration/framework"
    elif [[ "$SERVICE" == "marketplace" ]]; then
      DIR="$ROOT/marketplace/marketplace-repository/src/main/resources/db/migration/marketplace"
    else
      # Yagna-style: microservice_boot/{service}/{service}-repository/...
      DIR="$ROOT/${SERVICE}/${SERVICE}-repository/src/main/resources/db/migration/${SERVICE//-/_}"
      if [[ ! -d "$DIR" ]]; then
        DIR="$ROOT/${SERVICE}/${SERVICE}-repository/src/main/resources/db/migration/${SERVICE}"
      fi
    fi
    FILE="V${EPOCH}__${JIRA}.sql"
    ;;
  cassandra)
    EXT="cql"
    DIR="$ROOT/${SERVICE}/${SERVICE}-rest/src/main/resources/cassandra/migration"
    FILE="${EPOCH}_${JIRA}.cql"
    ;;
  *)
    echo "Unsupported db type: $DB_TYPE" >&2
    exit 1
    ;;
esac

mkdir -p "$DIR"
TARGET="$DIR/$FILE"

if [[ -f "$TARGET" ]]; then
  echo "File already exists: $TARGET" >&2
  exit 1
fi

cat > "$TARGET" <<EOF
-- ${JIRA}: ${DESC}
-- Created: $(date -u +"%Y-%m-%dT%H:%M:%SZ")
-- Naming: V{epoch_seconds}__{JIRA_ID}.sql (MySQL/PostgreSQL) or {epoch}_{JIRA_ID}.cql (Cassandra)

EOF

chmod +x "$0"
echo "Created: $TARGET"
