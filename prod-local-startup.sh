#!/usr/bin/env bash
# Start all Farm Kart microservices with spring.profiles.active=prod
# Requires production env vars (JWT_SECRET, DB URLs, etc.)
exec env SPRING_PROFILE=prod "$(dirname "$0")/dev-local-startup.sh" "$@"
