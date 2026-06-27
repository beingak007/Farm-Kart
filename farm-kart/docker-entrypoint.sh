#!/bin/sh
set -e

APP_FILE=${1:-farm-kart-rest.jar}
echo "Starting ${APP_FILE}"

if [ "${profile}" = "dev" ]; then
  PROFILE_ARG="-Dspring.profiles.active=dev"
else
  PROFILE_ARG=""
fi

exec java ${JAVA_OPTS} -jar ${PROFILE_ARG} /home/${APP_FILE}
