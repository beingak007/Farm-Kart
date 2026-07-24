#!/usr/bin/env bash
# Wrapper — run from repo root: ./scripts/kafka-infra.sh start
exec "$(cd "$(dirname "$0")/.." && pwd)/infra/kafka/scripts/kafka-infra.sh" "$@"
