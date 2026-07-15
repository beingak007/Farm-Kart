#!/bin/sh
set -e

APP_FILE=${1:-farm-kart-rest.jar}
echo "Starting ${APP_FILE}"

# Prod by default in containers; override with SPRING_PROFILES_ACTIVE=dev
ACTIVE_PROFILE="${SPRING_PROFILES_ACTIVE:-${profile:-prod}}"
echo "Spring profile: ${ACTIVE_PROFILE}"

exec java ${JAVA_OPTS} -Dspring.profiles.active="${ACTIVE_PROFILE}" -jar "/home/${APP_FILE}"
