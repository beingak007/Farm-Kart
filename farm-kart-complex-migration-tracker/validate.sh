#!/usr/bin/env bash
set -euo pipefail

ROOT="$(cd "$(dirname "$0")" && pwd)"
MIGRATIONS="$ROOT/src/main/resources/migrations"

check_file() {
  if [[ ! -f "$1" ]]; then
    echo "[ERROR] Missing mandatory file: $1" >&2
    exit 1
  fi
}

is_file_empty() {
  if [[ ! -s "$1" ]]; then
    echo "[ERROR] File is empty: $1" >&2
    exit 1
  fi
}

is_one_per_file() {
  local file="$1"
  local ext="${file##*.}"
  if [[ "$ext" == "sql" || "$ext" == "cql" ]]; then
    local count
    count=$(grep -ciE '^\s*(CREATE|DROP|ALTER)\s' "$file" || true)
    if [[ "$count" -gt 1 ]]; then
      echo "[ERROR] $file must contain at most one CREATE/DROP/ALTER statement (found $count)" >&2
      exit 1
    fi
  fi
}

if [[ ! -d "$MIGRATIONS" ]]; then
  echo "[INFO] No migrations directory yet — nothing to validate"
  exit 0
fi

cd "$MIGRATIONS"
for migration in */; do
  migration="${migration%/}"
  echo "[INFO] Validating $migration"
  cd "$migration"
  check_file order
  check_file keyspace
  is_file_empty order
  is_file_empty keyspace
  mapfile -t exec_order < order
  for file in "${exec_order[@]}"; do
    [[ -z "$file" ]] && continue
    if [[ ! -f "$file" ]]; then
      echo "[ERROR] $file listed in order but not found" >&2
      exit 1
    fi
    is_file_empty "$file"
    is_one_per_file "$file"
    echo "[INFO] OK $file"
  done
  cd ..
done

echo "[INFO] All complex migrations valid"
